package com.gospel.data.collection.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gospel.data.collection.bilibili.pojo.dto.RoomPreviewDTO;
import com.gospel.data.collection.bilibili.pojo.dto.RoomRankingDTO;
import com.gospel.data.collection.bilibili.pojo.dto.RoomRecommendDTO;
import com.gospel.data.collection.bilibili.pojo.entity.RoomOnline;
import com.gospel.data.collection.bilibili.pojo.entity.RoomPreview;
import com.gospel.data.collection.bilibili.pojo.entity.RoomRanking;
import com.gospel.data.collection.bilibili.repository.RoomOnlineRepository;
import com.gospel.data.collection.bilibili.repository.RoomPreviewRepository;
import com.gospel.data.collection.bilibili.repository.RoomRankingRepository;
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
            logger.info("[roomRecommend]\twait\t" + no + "\t" + waitTime);
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
        logger.info("[roomRecommend]\tres \t" + no);
        return result.toString();
    }

    private void saveData(String res) {
        JSONObject resJson = JSONObject.parseObject(res);
        JSONObject dataJson = resJson.getJSONObject("data");

        //统计数据
        RoomOnline roomOnline = new RoomOnline();
        roomOnline.setDynamic(dataJson.getInteger("dynamic"));            //动态条数
        roomOnline.setOnlineTotal(dataJson.getInteger("online_total"));  //在线总数(直播间个数)
        JSONObject text_linkJson = dataJson.getJSONObject("text_link");
        roomOnline.setLink(text_linkJson.getString("link"));
        roomOnline.setText(text_linkJson.getString("text"));
        roomOnline.setCreated(new Date());
        RoomOnlineRepository roomOnlineRepository = applicationContext.getBean(RoomOnlineRepository.class);
        roomOnlineRepository.save(roomOnline);
        roomOnlineRepository.flush();
        logger.info("[roomRecOnline]\tdbsave\t" + no + "\t数据添加成功");

        redisUtil = applicationContext.getBean(RedisUtil.class);
        //主页-正在直播-为你推荐-(活动)
        JSONArray previewJson = dataJson.getJSONArray("preview");
        List<RoomPreview> roomPreviewList = previewJson.toJavaList(RoomPreview.class);
        RoomPreviewRepository roomPreviewRepository = applicationContext.getBean(RoomPreviewRepository.class);
        for (int i = 0; i < roomPreviewList.size(); i++) {
            RoomPreview roomPreview = roomPreviewList.get(i);
            String roomPreviewDTOJson = JSONObject.toJSONString(roomPreview);
            if (redisUtil.sAdd("previewJson", roomPreviewDTOJson) > 0) {
                logger.info("[previewJson]\tresave\t" + no + "\t第" + i + "条数据添加成功");
                roomPreview.setCreated(new Date());
                roomPreviewRepository.save(roomPreview);
                roomPreviewRepository.flush();
                logger.info("[previewJson]\tdbsave\t" + no + "\t第" + i + "条数据添加成功");
            } else {
                logger.info("[previewJson]\tresave\t" + no + "\t第" + i + "条数据重复,拒绝添加");
            }
        }

        //直播排名
        JSONArray rankingJson = dataJson.getJSONArray("ranking");
        List<RoomRanking> roomRankingList = rankingJson.toJavaList(RoomRanking.class);
        RoomRankingRepository roomRankingRepository = applicationContext.getBean(RoomRankingRepository.class);
        for (int i = 0; i < roomRankingList.size(); i++) {
            RoomRanking roomRanking = roomRankingList.get(i);
            String roomRankingDTOJson = JSONObject.toJSONString(roomRanking);
            if (redisUtil.sAdd("rankingJson", roomRankingDTOJson) > 0) {
                logger.info("[rankingJson]\tresave\t" + no + "\t第" + i + "条数据添加成功");
                roomRanking.setCreated(new Date());
                roomRankingRepository.save(roomRanking);
                roomRankingRepository.flush();
                logger.info("[rankingJson]\tdbsave\t" + no + "\t第" + i + "条数据添加成功");
            } else {
                logger.info("[rankingJson]\tresave\t" + no + "\t第" + i + "条数据重复,拒绝添加");
            }
        }

        //主页-正在直播-推荐直播
        JSONArray recommendJson = dataJson.getJSONArray("recommend");
        List<RoomRecommendDTO> roomRecommendDTOS = recommendJson.toJavaList(RoomRecommendDTO.class);
        for (int i = 0; i < roomRecommendDTOS.size(); i++) {
            RoomRecommendDTO roomRecommendDTO = roomRecommendDTOS.get(i);
            String roomRecommendDTOJson = JSONObject.toJSONString(roomRecommendDTO);
            if (redisUtil.sAdd("recommendJson", roomRecommendDTOJson) > 0) {
                logger.info("[recommendJson]\tresave\t" + no + "\t第" + i + "条数据添加成功");
            } else {
                logger.info("[recommendJson]\tresave\t" + no + "\t第" + i + "条数据重复,拒绝添加");
            }
        }

    }
}
