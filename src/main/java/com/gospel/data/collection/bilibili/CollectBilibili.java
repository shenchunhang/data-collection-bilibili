package com.gospel.data.collection.bilibili;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 *项目名: data-collection-bilibili
 *文件名: CollectBilibili
 *创建者: SCH
 *创建时间:2019/5/27 11:23
 *描述: TODO
 */
@Service
public class CollectBilibili {

    public String collect(String path) {
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

//    public static void main(String[] args) {
//        collect("https://api.bilibili.com/x/web-interface/dynamic/region?callback=jqueryCallback_bili_011217614" +
//                "01943977&jsonp=jsonp&ps=15&rid=138&_=1558928420355");
//    }
}
