package com.lw.familysystem.book;

import com.lw.familysystem.book.mapper.BookInfoMapper;
import com.lw.familysystem.book.mapper.BookTitlesMapper;
import com.lw.familysystem.entity.BookInfo;
import com.lw.familysystem.entity.BookTitles;
import com.lw.familysystem.vo.BookInfoVo;
import com.lw.familysystem.vo.BookTitlesVo;
import com.lw.familysystem.vo.VideoCategoryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("bookRefreshService")
@Slf4j
public class BookRefreshService {

    @Autowired
    private BookInfoMapper bookInfoMapper;

    @Autowired
    private BookTitlesMapper bookTitlesMapper;

    @Value("${video.config.book_path:E:\\temp\\图书}")
    private String ROOT_PATH = "E:\\temp\\图书";

    /**
     * 刷新书籍信息
     */
    public void refreshBookInfo2DB() {
        List<BookInfoVo> bookInfoVos = this.bookInfoMapper.findAllBookInfo();
        File rootFile = new File(ROOT_PATH);
        if (rootFile.exists()) {
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isFile();
                }
            };
            File[] bookFiles = rootFile.listFiles(fileFilter);//只罗列出文件

            for (File bookFile : bookFiles) {
                String relativePath = bookFile.getAbsolutePath().substring(ROOT_PATH.length());

                boolean hasIn = false;//是否已经存在于DB中
                for (BookInfoVo bookInfoVo : bookInfoVos) {
                    if (relativePath.equals(bookInfoVo.getRelative_path())) {
                        hasIn = true;
                        break;
                    }
                }

                if (!hasIn) {//数据库没有的才添加
                    BookInfo bookInfo = new BookInfo(bookFile, ROOT_PATH);
                    this.bookInfoMapper.addBookInfo(bookInfo);
                }
            }
        }
    }

    /**
     * 刷新章节信息
     *
     * @param bookInfoVos
     */
    public void refreshBookTitles2DB(List<BookInfoVo> bookInfoVos) {
        if (bookInfoVos == null || bookInfoVos.size() == 0) {
            bookInfoVos = this.bookInfoMapper.findAllBookInfo();
        }
        for (BookInfoVo bookInfoVo : bookInfoVos) {
            //先删除对应的章节
            this.bookTitlesMapper.deleteBookTitlesByBookId(bookInfoVo.getBook_id());

            List<BookTitles> bookTitlesList = new ArrayList<>();

            File book = new File(ROOT_PATH + bookInfoVo.getRelative_path());
            try (RandomAccessFile targetFile = new RandomAccessFile(book, "r")) {
                targetFile.seek(0);
                String lineContent = null;

                int orderNo = 0;

                while ((lineContent = targetFile.readLine()) != null) {
                    lineContent = new String(lineContent.getBytes("ISO-8859-1"), "UTF-8");
                    if ((lineContent.matches("=.*\\d{1,5}[、|.|\\s]?\\w*.*=")
                            || lineContent.matches(".*第[\\w{1,5}|\\D{1,3}]章[、|.]?\\w*.*")
                            || lineContent.matches("==.*=="))
                            && lineContent.length() < 40) {
                        long titleRange = targetFile.getFilePointer() - lineContent.length() * 8;
                        BookTitles bookTitles = new BookTitles();
                        bookTitles.setBook_id(bookInfoVo.getBook_id());
                        bookTitles.setTitle_name(lineContent);
                        bookTitles.setTitle_range(titleRange);
                        bookTitles.setOrder_no(orderNo++);

                        bookTitlesList.add(bookTitles);

                        if(bookTitlesList.size()>100){
                            this.bookTitlesMapper.refreshBookTitles2DB(bookTitlesList);
                            bookTitlesList = new ArrayList<>();
                        }

                    }
                }
            } catch (IOException e) {
                log.error("", e);
            }

            if (bookTitlesList.size() > 0) {
                this.bookTitlesMapper.refreshBookTitles2DB(bookTitlesList);
            }

        }
    }
}
