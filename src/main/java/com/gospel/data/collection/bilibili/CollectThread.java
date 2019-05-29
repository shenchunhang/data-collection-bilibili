package com.gospel.data.collection.bilibili;

import cn.hutool.core.util.RandomUtil;
import com.gospel.data.collection.bilibili.util.RedisUtil;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 *项目名: data-collection-bilibili
 *文件名: CollectThread
 *创建者: SCH
 *创建时间:2019/5/28 22:37
 *描述: TODO
 */
public class CollectThread implements Runnable {
    private String path;
    private int no;
    private int ranM;
    private int ranS;
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public void run() {
        no = 0;
        RedisUtil redisUtil = applicationContext.getBean(RedisUtil.class);
        while (true) {
            if (no > 1000) {
                no = 0;
            }
            String data = collect(path);
            redisUtil.set("d" + no, data);
            no++;
            ranM = RandomUtil.randomInt(1, 3);
            ranS = RandomUtil.randomInt(30, 65);
            try {
                System.out.println("等待:\t" + (1000 * ranS * ranM));
                Thread.sleep(1000 * ranS * ranM);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String collect(String path) {
        BufferedReader in = null;
        StringBuffer result = null;
        try {
            URL url = new URL(path);
            //建立连接对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            //connection.setRequestProperty("Referer", "https://www.bilibili.com/v/life/?spm_id_from=333.334.b_7072696d6172795f6d656e75.58");
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
}
