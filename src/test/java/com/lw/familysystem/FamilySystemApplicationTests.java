package com.lw.familysystem;

import com.lw.familysystem.book.BookRefreshService;
import com.lw.familysystem.book.mapper.BookInfoMapper;
import com.lw.familysystem.video.VideoRefreshService;
import com.lw.familysystem.vo.BookInfoVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FamilySystemApplicationTests {

    @Resource(name = "videoRefreshService")
    private VideoRefreshService refreshService;

    @Resource(name = "bookRefreshService")
    private BookRefreshService bookRefreshService;
    @Autowired
    private BookInfoMapper bookInfoMapper;

    @Test
    public void bookRefreshTest(){
        bookRefreshService.refreshBookInfo2DB();
    }

    @Test
    public void bookTitleRefreshTest(){
        BookInfoVo bookInfoVo = this.bookInfoMapper.findBookById(6);
        List<BookInfoVo> bookInfoVos = new ArrayList<>();
        bookInfoVos.add(bookInfoVo);
        bookRefreshService.refreshBookTitles2DB(bookInfoVos);
    }

    @Test
    public void videoPhysicsRefreshTest() {
        refreshService.refreshVideoPhysicsInfo2Db();
    }

    @Test
    public void videoRefreshInfoTest() {
        refreshService.refreshVideoInfo2Db();
    }
    @Test
    public void videoCategoryInfoTest() {
        refreshService.refreshCategoryInfo2Db();
    }

}
