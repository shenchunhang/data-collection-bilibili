package com.gospel.data.collection.bilibili.pojo.dto;

import lombok.Data;

import java.security.PrivateKey;

/*
 *项目名: data-collection-bilibili
 *文件名: RankingDTO
 *创建者: SCH
 *创建时间:2019/6/2 16:33
 *描述: TODO
 */
@Data
public class RankingDTO {
    private String face;        //封面链接
    private String link;        //视频链接
    private String online;      //直播间人数
    private String roomid;      //直播间ID
    private String title;       //标题
    private String uname;       //用户名
}
