package com.gospel.data.collection.bilibili.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.gospel.data.collection.bilibili.pojo.entity.Online;
import com.gospel.data.collection.bilibili.repository.OnlineRepository;
import com.gospel.data.collection.bilibili.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/*
 *项目名: data-collection-bilibili
 *文件名: SearchBoxThread
 *创建者: SCH
 *创建时间:2019/5/30 16:00
 *描述: 搜索框统计
 */
public class SearchBoxThread extends Thread {
    private String path = "https://api.bilibili.com/x/web-interface/online?callback=jqueryCallback_bili_18984301554370875&jsonp=jsonp&_=1559098307094";
    private ApplicationContext applicationContext;
    private RedisUtil redisUtil;
    private int no;
    private int ranM;
    private int ranS;
    private int waitTime;

    public SearchBoxThread(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public SearchBoxThread(ApplicationContext applicationContext, String path) {
        this.applicationContext = applicationContext;
        this.path = path;
    }

    @Override
    public void run() {
        no = 1;
        Logger logger = LoggerFactory.getLogger(getClass());
        while (true) {
            String res = collect(path);
            long nowTime = System.currentTimeMillis();
            Date now = new Date();
            logger.info("res \t" + no + "\t" + now);
//            System.out.println("res \t" + no + "\t" + now);
            saveData(res, no);
            logger.info("save\t" + no + "\t" + now);
//            System.out.println("save\t" + no + "\t" + now);
            ranM = RandomUtil.randomInt(1, 10);
            ranS = RandomUtil.randomInt(10, 20);
            waitTime = 1000 * 60 * 1;
            logger.info("wait\t" + no + "\t" + now + "\t");
//            System.out.println("wait\t" + no + "\t" + now + "\t");
            no++;
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String collect(String path) {
        BufferedReader in = null;
        StringBuffer result = null;
        try {
            URL url = new URL(path);
            //建立连接对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Referer", "https://www.bilibili.com/v/life/?spm_id_from=333.334.b_7072696d6172795f6d656e75.58");
            connection.connect();
            result = new StringBuffer();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private void saveData(String res, int no) {
        String str = res.substring(res.indexOf("({") + 1, res.indexOf("})") + 1);
        Online online = new Online();
        JSONObject resJson = JSONObject.parseObject(str);
        JSONObject dataJson = JSONObject.parseObject(resJson.get("data").toString());
        String dataJsonStr = dataJson.toJSONString();

        redisUtil = applicationContext.getBean(RedisUtil.class);
        redisUtil.set("searchBox" + no, dataJson.toJSONString());
        Date now = new Date();
        online.setCreated(now);
        online.setYear(now.getYear() + 1900);
        online.setMonth(now.getMonth() + 1);
        online.setDay(now.getDate());
        OnlineRepository dao = applicationContext.getBean(OnlineRepository.class);
        dao.save(online);
        dao.flush();
    }
}
