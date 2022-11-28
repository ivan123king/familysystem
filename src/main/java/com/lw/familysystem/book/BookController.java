package com.lw.familysystem.book;

import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.ReturnInfo;
import com.lw.familysystem.entity.BookInfo;
import com.lw.familysystem.entity.BookTitles;
import com.lw.familysystem.vo.BookDetailVo;
import com.lw.familysystem.vo.BookInfoVo;
import com.lw.familysystem.vo.BookTitlesVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/book")
public class BookController {

    @Resource(name="bookService")
    private BookService bookService;

    @GetMapping("/findBookInfoByPage")
    @ResponseBody
    public ReturnInfo findBookInfoByPage(@RequestParam("bookName")String bookName, HttpServletRequest request){
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        PageRequest pageRequest = new PageRequest(Integer.parseInt(page),Integer.parseInt(size));

        BookInfo bookInfo = new BookInfo();
        bookInfo.setBook_name(bookName);
        Page<BookInfoVo> pageData = this.bookService.findBookInfoByPage(bookInfo,pageRequest);

        return ReturnInfo.returnSuccessInfo(pageData);
    }
    @GetMapping("/getBookContent/{bookId}")
    @ResponseBody
    public ReturnInfo getBookContent(@PathVariable("bookId")int bookId,HttpServletRequest request){
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        String range = request.getParameter("range");

        BookDetailVo bookDetailVo = this.bookService.getBookContent(bookId,Integer.parseInt(page),Integer.parseInt(size),Long.parseLong(range));

        return ReturnInfo.returnSuccessInfo(bookDetailVo);
    }
    @GetMapping("/findBookTitlesByPage/{bookId}")
    @ResponseBody
    public ReturnInfo findBookTitlesByPage(@PathVariable("bookId") int bookId,HttpServletRequest request){
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        PageRequest pageRequest = new PageRequest(Integer.parseInt(page),Integer.parseInt(size));

        BookTitles bookTitles = new BookTitles();
        bookTitles.setBook_id(bookId);

        Page<BookTitlesVo> pageData = this.bookService.findBookTitlesByPage(bookTitles,pageRequest);

        return ReturnInfo.returnSuccessInfo(pageData);
    }
}
