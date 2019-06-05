package com.gospel.data.collection.bilibili.service;

import com.gospel.data.collection.bilibili.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Date;

/*
 *项目名: data-collection-bilibili
 *文件名: PgcWebRankListThread
 *创建者: SCH
 *创建时间:2019/6/4 22:33
 *描述: TODO
 */
public class PgcWebRankListThread extends Thread {
    private String path = "https://api.bilibili.com/pgc/web/rank/list?season_type=1&day=3";
    private ApplicationContext applicationContext;
    private RedisUtil redisUtil;
    private Logger logger;
    private int no;
    private int ranM;
    private int ranS;
    private int waitTime;
    private String seasonType = "1";
    private String day = "3";

    public PgcWebRankListThread(ApplicationContext applicationContext, String seasonType, String day) {
        this.applicationContext = applicationContext;
        this.seasonType = seasonType;
        this.day = day;
    }

    public PgcWebRankListThread(ApplicationContext applicationContext, String path) {
        this.applicationContext = applicationContext;
        this.path = path;
    }

    @Override
    public void run() {
        path = "https://api.bilibili.com/pgc/web/rank/list?season_type=" + seasonType + "&day=" + day;
        no = 1;
        logger = LoggerFactory.getLogger(Logger.class);
        while (true) {
//            String res = collect(path);
            Date now = new Date();
//            saveData(res);
            waitTime = 1000 * 60 * 1;
            logger.info("[roomRecommend]\twait\t" + no + "\t" + waitTime);
            no++;
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
