package com.lw.familysystem;

import org.junit.Test;

import java.io.File;

public class JunitTest {

    @Test
    public void testFilePath() throws Exception {
        String rootPath = "E:\\temp\\视频";
        File file = new File(rootPath);
        for (File f : file.listFiles()) {
            String filePath = f.getAbsolutePath();
            System.out.println(filePath.substring(filePath.indexOf(rootPath)+rootPath.length()));
        }
    }
}
