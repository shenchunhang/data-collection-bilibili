package com.gospel.data.collection.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gospel.data.collection.bilibili.pojo.dto.PreviewDTO;
import com.gospel.data.collection.bilibili.pojo.dto.RankingDTO;
import com.gospel.data.collection.bilibili.pojo.dto.RecommendDTO;
import com.gospel.data.collection.bilibili.pojo.entity.RoomRecOnline;
import com.gospel.data.collection.bilibili.repository.RoomRecOnlineRepository;
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
import java.util.List;

/*
 *项目名: data-collection-bilibili
 *文件名: RoomRecommendThread
 *创建者: SCH
 *创建时间:2019/6/2 11:34
 *描述: 直播排名
 */
public class RoomRecommendThread extends Thread {

    private String path = "https://api.live.bilibili.com/room/v1/RoomRecommend/biliIndexRecList";
    private ApplicationContext applicationContext;
    private RedisUtil redisUtil;
    private Logger logger;
    private int no;
    private int ranM;
    private int ranS;
    private int waitTime;

    public RoomRecommendThread(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public RoomRecommendThread(ApplicationContext applicationContext, String path) {
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
        logger.info("[online]\tres \t" + no + "\t" + result);
        return result.toString();
    }

    private void saveData(String res) {
        JSONObject resJson = JSONObject.parseObject(res);
        JSONObject dataJson = JSONObject.parseObject(resJson.getString("data"));

        //统计数据
        RoomRecOnline roomRecOnline = new RoomRecOnline();
        roomRecOnline.setDynamic(dataJson.getInteger("dynamic"));            //动态条数
        roomRecOnline.setOnlineTotal(dataJson.getInteger("online_total"));  //在线总数(直播间个数)
        roomRecOnline.setLink(dataJson.getString("link"));
        roomRecOnline.setText(dataJson.getString("text"));
        Date now = new Date();
        roomRecOnline.setCreated(now);
        roomRecOnline.setYear(now.getYear() + 1900);
        roomRecOnline.setMonth(now.getMonth() + 1);
        roomRecOnline.setDay(now.getDate());
        RoomRecOnlineRepository roomRecOnlineRepository = applicationContext.getBean(RoomRecOnlineRepository.class);
        roomRecOnlineRepository.save(roomRecOnline);
        roomRecOnlineRepository.flush();
        logger.info("[roomRecOnline]\tdbsave\t" + no + "\t数据添加成功");


        redisUtil = applicationContext.getBean(RedisUtil.class);
        //主页-正在直播-为你推荐-(活动)
        JSONArray previewJson = dataJson.getJSONArray("preview");
        List<PreviewDTO> previewDTOList = previewJson.toJavaList(PreviewDTO.class);
        for (int i = 0; i < previewDTOList.size(); i++) {
            if (redisUtil.sAdd("previewJson", previewDTOList.get(i).toString()) > 0) {

            }
        }


        //直播排名

        JSONArray rankingJson = dataJson.getJSONArray("ranking");
        List<RankingDTO> rankingDTOList = rankingJson.toJavaList(RankingDTO.class);
        for (int i = 0; i < rankingDTOList.size(); i++) {
            if (redisUtil.sAdd("rankingJson", rankingDTOList.get(i).toString()) > 0) {

            }
        }


        //主页-正在直播-推荐直播
        JSONArray recommendJson = dataJson.getJSONArray("recommend");
        List<RecommendDTO> recommendDTOList = recommendJson.toJavaList(RecommendDTO.class);
        for (int i = 0; i < rankingDTOList.size(); i++) {
            if (redisUtil.sAdd("recommendJson", recommendDTOList.get(i).toString()) > 0) {

            }
        }

    }
}
