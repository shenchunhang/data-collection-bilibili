package com.gospel.data.collection.bilibili;

import com.gospel.data.collection.bilibili.service.OnlineThead;
import com.gospel.data.collection.bilibili.service.RoomRecommendThread;
import com.gospel.data.collection.bilibili.service.SearchBoxThread;
import com.gospel.data.collection.bilibili.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        Logger logger = LoggerFactory.getLogger(Logger.class);
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();//通用springIOC上下文对象
        try {
            new OnlineThead(applicationContext).start();//online 接口
            new SearchBoxThread(applicationContext).start();//searchBox 接口
            new RoomRecommendThread(applicationContext).start();//Roomrecommond 接口
        } catch (Exception ex) {
            logger.error("出错了" + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
