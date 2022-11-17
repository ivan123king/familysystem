package com.lw.test;

import io.swagger.models.auth.In;

import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.*;

public class ThreadTest {
    static String DB_URL = "";

    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>() {
        public Connection initialValue() {
            try {
                return DriverManager.getConnection(DB_URL);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void remove() {
            super.remove();
        }
    };

    public static Connection getConnection() {
        return connectionHolder.get();
    }


    /**
     * 自定义方法  异步加载
     * @return
     */
    public int loadDataAsync(){
        System.out.println("loadDataAsync： load data start.....");
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {

        }
        System.out.println("loadDataAsync： load data end.....");
        return 30;
    }

    /**
     * 同步加载数据方法
     * @return
     */
    public int loadDataSync(){
        System.out.println("loadDataSync： load data start.....");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }
        System.out.println("loadDataSync： load data end.....");
        return 30;
    }

//    private final FutureTask<Integer> future =
//            new FutureTask<Integer>(new Callable<Integer>() {
//                public Integer call(){
//                    return loadDataAsync();
//                }
//            });
//    private final Thread thread = new Thread(future);
//
//    public void start() {
//        thread.start();
//    }
//
//    public Integer get() throws  InterruptedException {
//        try {
//            return future.get();
//        } catch (ExecutionException e) {
//            Throwable cause = e.getCause();
//        }
//        return 0;
//    }
//
//    public static void main(String[] args) {
//        ThreadTest threadTest = new ThreadTest();
//        try {
//            threadTest.loadDataSync();
//            threadTest.start();
//            int i = threadTest.get();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }


    private Future<Integer> getData(){
        CompletableFuture<Integer> future = new CompletableFuture<>();
        new Thread(()->{
           int dataTotal = loadDataAsync();
           future.complete(dataTotal);
        }).start();
        return future;
    }

    public static void main(String[] args) {
        ThreadTest threadTest = new ThreadTest();
        Future<Integer> future = threadTest.getData();
        try {
            System.out.println("start");
            int dataTotal = future.get().intValue();
            System.out.println(dataTotal);
        } catch (ExecutionException| InterruptedException e) {
            e.printStackTrace();
        }
    }


}
