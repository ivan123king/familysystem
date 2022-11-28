package com.lw.familysystem;

import com.lw.familysystem.book.BookRefreshService;
import com.lw.familysystem.video.VideoRefreshService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FamilySystemApplicationTests {

    @Resource(name = "videoRefreshService")
    private VideoRefreshService refreshService;

    @Resource(name = "bookRefreshService")
    private BookRefreshService bookRefreshService;

    @Test
    public void bookRefreshTest(){
        bookRefreshService.refreshBookInfo2DB();
    }

    @Test
    public void bookTitleRefreshTest(){
        bookRefreshService.refreshBookTitles2DB(null);
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
