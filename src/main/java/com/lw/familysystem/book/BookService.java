package com.lw.familysystem.book;

import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.vo.BookDetailVo;
import com.lw.familysystem.vo.BookInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * 图书
 */
@Service("bookService")
@Slf4j
public class BookService {

    @Value("${video.config.book_path:E:\\temp\\图书}")
    private String ROOT_PATH = "E:\\temp\\图书";

    /**
     * 文件内容获取  TXT格式
     * @param filePath
     * @return
     */
    public BookDetailVo getBookContent(String filePath, int page,int size,long range){
        BookDetailVo bookDetailVo = new BookDetailVo();

        File book = new File(ROOT_PATH+filePath);
        try(RandomAccessFile targetFile = new RandomAccessFile(book, "r")) {
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            targetFile.seek(range);
            StringBuilder sb = new StringBuilder();
            String lineContent = null;

           long bookLength = targetFile.length();
           bookDetailVo.setBookLength(bookLength);

            while ((lineContent = targetFile.readLine())!=null) {
                lineContent = new String(lineContent.getBytes("ISO-8859-1"),"UTF-8");
                sb.append(lineContent+"\r\n");
                range = targetFile.getFilePointer();
                if(sb.length()>size||range>=bookLength) break;
            }
            bookDetailVo.setContent(sb.toString());
            bookDetailVo.setRange(range);
        } catch (IOException e) {
           log.error("",e);
        }
        return bookDetailVo;
    }

    /**
     * 获取标题
     * @param filePath
     * @param size
     * @param range
     * @return
     */
    public BookInfoVo getBookTitles(String filePath,int size,long range){
        BookInfoVo bookInfoVo = new BookInfoVo();
        File book = new File(ROOT_PATH+filePath);
        try(RandomAccessFile targetFile = new RandomAccessFile(book, "r")) {
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            targetFile.seek(range);
            StringBuilder sb = new StringBuilder();
            String lineContent = null;

            long bookLength = targetFile.length();
            bookInfoVo.setBookLength(bookLength);
            while ((lineContent = targetFile.readLine())!=null) {
                lineContent = new String(lineContent.getBytes("ISO-8859-1"),"UTF-8");
                if((lineContent.matches("=.*\\d{1,5}[、|.|\\s]?\\w*.*=")
                        ||lineContent.matches(".*第[\\w{1,5}|\\D{1,3}]章[、|.]?\\w*.*")
                        ||lineContent.matches("==.*=="))
                        &&lineContent.length()<40){
                    long titleRange = targetFile.getFilePointer()-lineContent.length()*8;
                    bookInfoVo.addTitle(lineContent,titleRange);
                    System.out.println(lineContent);
                }
            }
        } catch (IOException e) {
            log.error("",e);
        }
        return bookInfoVo;
    }

    public static void main(String[] args) {
        BookService bookService = new BookService();
        String filePath = "\\《开局百万灵石》.txt";
        filePath = "\\《人欲》.txt";
        int page = 0;
        int size = 10;
        long rang = page*size;
//        BookInfoVo bookInfoVo = bookService.getBookTitles(filePath,230,0);
//        System.out.println(bookInfoVo);

    }
}
