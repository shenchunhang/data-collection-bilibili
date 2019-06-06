package com.gospel.data.collection.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.gospel.data.collection.bilibili.pojo.entity.Online;
import com.gospel.data.collection.bilibili.repository.OnlineRepository;
import io.netty.util.internal.StringUtil;
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
 *文件名: OnlineThead
 *创建者: SCH
 *创建时间:2019/5/29 9:37
 *描述: 获取 bilibili 在线人数,各分区投稿数
 */
public class OnlineThead extends Thread {
    private String path = "https://api.bilibili.com/x/web-interface/online?callback=jqueryCallback_bili_18984301554370875&jsonp=jsonp&_=1559098307094";
    private ApplicationContext applicationContext;
    private Logger logger;
    private int no;
    private int ranM;
    private int ranS;
    private int waitTime;

    public OnlineThead(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public OnlineThead(ApplicationContext applicationContext, String path) {
        this.applicationContext = applicationContext;
        this.path = path;
    }

    @Override
    public void run() {
        no = 1;
        logger = LoggerFactory.getLogger(Logger.class);
        while (true) {
            String res = collect(path);
            Date now = new Date();
            saveData(res);
            waitTime = 1000 * 60 * 1;
            logger.info("[online]\twait\t" + no + "\t" + waitTime);
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
        logger.info("[online]\tres \t" + no + "\t");
        return result.toString();
    }

    private void saveData(String res) {
        String str = res.substring(res.indexOf("({") + 1, res.indexOf("})") + 1);
        Online online = new Online();
        JSONObject resJson = JSONObject.parseObject(str);
        JSONObject dataJson = JSONObject.parseObject(resJson.getString("data"));
        if (dataJson.isEmpty()) {
            logger.warn("[online]\tdbsave\t" + no + "\tB站返回数据异常");
        }
        online.setWebOnline(dataJson.getInteger("web_online"));
        online.setPlayOnline(dataJson.getInteger("play_online"));
        online.setAllCount(dataJson.getInteger("all_count"));
        JSONObject regionJson = dataJson.getJSONObject("region_count");
        online.setAnimaCount(regionJson.getInteger("1"));           //动画区投稿数(1)
        online.setMusicCount(regionJson.getInteger("3"));           //音乐区投稿数(3)
        online.setGanmeCount(regionJson.getInteger("4"));           //游戏区投稿数(4)
        online.setVarietyCount(regionJson.getInteger("5"));         //娱乐区投稿数(5)
        online.setDramaCount(regionJson.getInteger("13"));          //番剧区投稿数(13)
        online.setPart23count(regionJson.getInteger("23"));         //未知分区23投稿数(23)
        online.setScienceCount(regionJson.getInteger("36"));        //科技区投稿数(36)
        online.setGuichuCount(regionJson.getInteger("119"));        //鬼畜区投稿数(119)
        online.setDanceCount(regionJson.getInteger("129"));         //舞蹈区投稿数(129)
        online.setFashionCount(regionJson.getInteger("155"));       //时尚区投稿数(155)
        online.setLifeCount(regionJson.getInteger("160"));          //生活区投稿数(160)
        online.setAdCount(regionJson.getInteger("165"));            //广告区投稿数(165)
        online.setGuochuangCount(regionJson.getInteger("167"));     //国创区投稿数(167)
        online.setProjectionCount(regionJson.getInteger("177"));    //放映厅区投稿数(177)
        online.setMovieCount(regionJson.getInteger("181"));         //影视区投稿数(181)
        online.setDigitalCount(regionJson.getInteger("188"));       //数码区投稿数(188)
        online.setCreated(new Date());
        OnlineRepository dao = applicationContext.getBean(OnlineRepository.class);
        dao.save(online);
        dao.flush();
        logger.info("[online]\tdbsave\t" + no + "\t数据添加成功");
    }
}
