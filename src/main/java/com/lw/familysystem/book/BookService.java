package com.lw.familysystem.book;

import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.book.mapper.BookInfoMapper;
import com.lw.familysystem.book.mapper.BookTitlesMapper;
import com.lw.familysystem.entity.BookInfo;
import com.lw.familysystem.entity.BookTitles;
import com.lw.familysystem.vo.BookDetailVo;
import com.lw.familysystem.vo.BookInfoVo;
import com.lw.familysystem.vo.BookTitlesVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

/**
 * 图书
 */
@Service("bookService")
@Slf4j
public class BookService {

    @Value("${video.config.book_path:E:\\temp\\图书}")
    private String ROOT_PATH = "E:\\temp\\图书";

    @Autowired
    private BookInfoMapper bookInfoMapper;
    @Autowired
    private BookTitlesMapper bookTitlesMapper;

    public Page<BookInfoVo> findBookInfoByPage(BookInfo bookInfo,PageRequest pageRequest){
        Page<BookInfoVo> pageData = this.bookInfoMapper.findBookInfoByPage(bookInfo,pageRequest);
        return pageData;
    }

    /**
     * 文件内容获取  TXT格式
     * @return
     */
    public BookDetailVo getBookContent(int bookId, int page,int size,long range)  {
        BookDetailVo bookDetailVo = new BookDetailVo();
        bookDetailVo.setBookId(bookId);

        BookInfoVo bookInfoVo = this.bookInfoMapper.findBookById(bookId);
        String filePath = bookInfoVo.getRelative_path();


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
     * @return
     */
    public Page< BookTitlesVo> findBookTitlesByPage(BookTitles bookTitles,PageRequest pageRequest){
        Page< BookTitlesVo> pageData = this.bookTitlesMapper.findBookTitlesByPage(bookTitles,pageRequest);
        return pageData;
    }

    public static void main(String[] args) throws Exception {
        BookService bookService = new BookService();
        String filePath = "\\《开局百万灵石》.txt";
        filePath = "\\《人欲》.txt";

        filePath = "E:\\temp\\1.txt";
        File file = new File(filePath);
        RandomAccessFile fileR = new RandomAccessFile(file,"r");
        fileR.seek(8);
        String line = fileR.readLine();
//        line = fileR.readLine();
//        line = fileR.readLine();
        line = new String(line.getBytes("ISO-8859-1"),"UTF-8");
        log.info("length:"+line.length()+"\r\n"+line);
        log.info("pointer:"+fileR.getFilePointer());
    }
}
