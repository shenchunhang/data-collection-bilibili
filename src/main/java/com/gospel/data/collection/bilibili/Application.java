package com.gospel.data.collection.bilibili;

import com.gospel.data.collection.bilibili.service.OnlineThead;
import com.gospel.data.collection.bilibili.service.SearchBoxThread;
import com.gospel.data.collection.bilibili.util.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        ApplicationContext applicationContext = SpringUtil.getApplicationContext();//通用springIOC上下文对象
        new OnlineThead(applicationContext).start();//online 接口
        new SearchBoxThread(applicationContext).start();//searchBox 接口
    }

}
