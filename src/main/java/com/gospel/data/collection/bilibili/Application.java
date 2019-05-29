package com.gospel.data.collection.bilibili;

import com.gospel.data.collection.bilibili.service.OnlineThead;
import com.gospel.data.collection.bilibili.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        OnlineThead onlineThead =new OnlineThead(applicationContext,"https://api.bilibili.com/x/web-interface/online?callback=jqueryCallback_bili_18984301554370875&jsonp=jsonp&_=1559098307094");
        onlineThead.start();
//        CollectThread collectThread = new CollectThread();
//        collectThread.setApplicationContext(applicationContext);
//        collectThread.setPath("https://api.bilibili.com/x/web-interface/dynamic/region?callback=jqueryCallback_bili_5715392394722729&jsonp=jsonp&ps=10&rid=3&_=1559053160000");
//        new Thread(collectThread).start();
    }

}
