package com.lw.familysystem.book;

import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.ReturnInfo;
import com.lw.familysystem.vo.BookDetailVo;
import com.lw.familysystem.vo.BookInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/book")
public class BookController {

    @Resource(name="bookService")
    private BookService bookService;

    @GetMapping("/getBookContent")
    @ResponseBody
    public ReturnInfo getBookContent(HttpServletRequest request){
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        String range = request.getParameter("range");

        String filePath = "\\《人欲》.txt";

        BookDetailVo bookDetailVo = this.bookService.getBookContent(filePath,Integer.parseInt(page),Integer.parseInt(size),Long.parseLong(range));

        return ReturnInfo.returnSuccessInfo(bookDetailVo);
    }
    @GetMapping("/getBookTitles")
    @ResponseBody
    public ReturnInfo getBookTitles(HttpServletRequest request){
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        String range = request.getParameter("range");

        String filePath = "\\《人欲》.txt";

        BookInfoVo bookInfoVo = this.bookService.getBookTitles(filePath,Integer.parseInt(size),Long.parseLong(range));

        return ReturnInfo.returnSuccessInfo(bookInfoVo);
    }
}
