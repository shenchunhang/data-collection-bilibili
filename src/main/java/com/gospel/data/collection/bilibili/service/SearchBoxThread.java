package com.gospel.data.collection.bilibili.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.gospel.data.collection.bilibili.pojo.entity.SearchBox;
import com.gospel.data.collection.bilibili.repository.SearchBoxRepository;
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
    private String path = "https://api.bilibili.com/x/web-interface/search/default";
    private ApplicationContext applicationContext;
    private RedisUtil redisUtil;
    private Logger logger;
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
        logger = LoggerFactory.getLogger(getClass());
        while (true) {
            String res = collect(path);
            long nowTime = System.currentTimeMillis();
            Date now = new Date();
            saveData(res, no);
            ranM = RandomUtil.randomInt(1, 10);
            ranS = RandomUtil.randomInt(10, 20);
            waitTime = 1000 * 60 * 1;
            logger.info("[searchBox]\twait\t" + no + "\twaitTime:\t" + waitTime);
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

        logger.info("[searchBox]\tres \t" + no + "\t" + result);
        return result.toString();
    }

    private void saveData(String res, int no) {
        JSONObject resJson = JSONObject.parseObject(res);
        JSONObject dataJson = JSONObject.parseObject(resJson.get("data").toString());
        String showName = (String) dataJson.get("show_name");
        String dataJsonStr = dataJson.toJSONString();
        redisUtil = applicationContext.getBean(RedisUtil.class);
        if (redisUtil.sAdd("searchBoxShowName", showName) > 0) {
            if (redisUtil.sAdd("searchBox", dataJsonStr) > 0) {
                logger.info("[searchBox]\trdsave\t" + no + "\t" + new Date());
                SearchBox searchBox = new SearchBox();
                searchBox.setDataId(dataJson.getLong("id"));
                searchBox.setShowName(dataJson.getString("show_name"));
                searchBox.setName(dataJson.getString("name"));
                searchBox.setSeid(dataJson.getString("seid"));
                searchBox.setType(dataJson.getInteger("type"));
                searchBox.setCreated(new Date());
                SearchBoxRepository repository = applicationContext.getBean(SearchBoxRepository.class);
                repository.save(searchBox);
                repository.flush();
                logger.info("[searchBox]\tdbsave\t" + no + "\t新数据添加成功");
            }
        } else {
            logger.info("[searchBox]\tsavefail\t" + no + "\t数据重复,拒绝添加");
        }
    }
}
