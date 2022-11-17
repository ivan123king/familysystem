package com.lw.config;

import com.lw.familysystem.cache.FamilyCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 容器操作服务
 */
@Slf4j
@Component
public class ContextOperConfig implements ApplicationListener<ApplicationEvent> {

    /**
     * 从硬盘中读取对象
     */
    public void loadVideoHistoryFromHard(){
        FamilyCache.loadVideoHistoryFromHard();
    }

    /**
     * 将对象写入硬盘
     */
    public void writeVideoHistoryToHard(){
        FamilyCache.writeVideoHistoryToHard();
    }

//    @PostConstruct
//    public void init() {
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            log.info("容器销毁前善后处理");
//            writeVideoHistoryToHard();
//        }));
//    }

    /**
     * 容器开启，关闭操作
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
//        if(event instanceof  ContextClosedEvent){
//            log.info("容器销毁前处理");
//            writeVideoHistoryToHard();
//        }else if(event instanceof ApplicationStartedEvent){
//            log.info("容器启动前处理");
//            loadVideoHistoryFromHard();
//        }
    }
}
