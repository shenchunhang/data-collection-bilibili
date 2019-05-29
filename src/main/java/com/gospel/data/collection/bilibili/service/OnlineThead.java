package com.gospel.data.collection.bilibili.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.gospel.data.collection.bilibili.pojo.entity.Online;
import com.gospel.data.collection.bilibili.repository.OnlineRepository;
import com.gospel.data.collection.bilibili.util.RedisUtil;
import org.springframework.context.ApplicationContext;

import javax.xml.crypto.Data;
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
 *描述: TODO
 */
public class OnlineThead extends Thread {
    private String path;
    private ApplicationContext applicationContext;
    private RedisUtil redisUtil;
    private int no;
    private int ranM;
    private int ranS;
    private int waitTime;


    public OnlineThead(ApplicationContext applicationContext, String path) {
        this.applicationContext = applicationContext;
        this.path = path;
        this.redisUtil = (RedisUtil) applicationContext.getBean(RedisUtil.class);
    }

    @Override
    public void run() {
        while (true) {
            String res = collect(path);
            long nowTime = System.currentTimeMillis();
            Date now = new Date();
            System.out.println("res\t\t\t" + now);
//            redisUtil.set("online" + nowTime, res);
            saveData(res);
            System.out.println("save\t\t" + now);
            ranM = RandomUtil.randomInt(1, 10);
            ranS = RandomUtil.randomInt(10, 20);
            waitTime = 1000 * ranS * ranM;
            System.out.println("wait:" + waitTime + "\t" + now);
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

    private void saveData(String res) {
        String str = res.substring(res.indexOf("({") + 1, res.indexOf("})") + 1);
        Online online = new Online();
        JSONObject resJson = JSONObject.parseObject(str);
        JSONObject dataJson = JSONObject.parseObject(resJson.get("data").toString());
        online.setWebOnline((int) dataJson.get("web_online"));
        online.setPlayOnline((int) dataJson.get("play_online"));
        online.setAllCount((int) dataJson.get("all_count"));
        JSONObject regionJson = dataJson.getJSONObject("region_count");
        System.out.println("regionJson\t" + regionJson);
        online.setAnimaCount((int) regionJson.get("1"));       //动画区投稿数(1)
        online.setDramaCount((int) regionJson.get("13"));      //番剧区投稿数(13)
        online.setGuochuangCount((int) regionJson.get("13"));  //国创区投稿数(167)
        online.setMusicCount((int) regionJson.get("13"));      //音乐区投稿数
        online.setDanceCount((int) regionJson.get("129"));     //舞蹈区投稿数(129)
        online.setGanmeCount((int) regionJson.get("13"));      //游戏区投稿数
        online.setScienceCount((int) regionJson.get("13"));    //科技区投稿数
        online.setDigitalCount((int) regionJson.get("188"));   //数码区投稿数(188)
        online.setLifeCount((int) regionJson.get("13"));       //生活区投稿数
        online.setGuichuCount((int) regionJson.get("119"));    //鬼畜区投稿数(119)
        online.setFashionCount((int) regionJson.get("155"));   //时尚区投稿数(155)
        online.setAdCount((int) regionJson.get("13"));         //广告区投稿数(165)
        online.setVarietyCount((int) regionJson.get("13"));    //娱乐区投稿数
        online.setMovieCount((int) regionJson.get("13"));      //影视区投稿数
        online.setProjectionCount((int) regionJson.get("13")); //放映厅区投稿数
        Date now = new Date();
        online.setCreated(now);
        online.setYear(now.getYear());
        online.setMonth(now.getMonth()+1);
        online.setDay(now.getDate()+1);
        OnlineRepository dao = applicationContext.getBean(OnlineRepository.class);
        dao.save(online);
        dao.flush();
    }
}
