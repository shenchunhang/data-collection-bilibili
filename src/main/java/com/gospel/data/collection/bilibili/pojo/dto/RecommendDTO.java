package com.gospel.data.collection.bilibili.pojo.dto;

import java.util.Date;

/*
 *项目名: data-collection-bilibili
 *文件名: RecommendDTO
 *创建者: SCH
 *创建时间:2019/6/2 16:55
 *描述: TODO
 */
public class RecommendDTO {
    private String areaId;              //直播分区ID
    private String areaName;            //直播分区名字
    private String area_v2_id;          //直播分区ID
    private String area_v2_name;        //直播分区名字
    private String area_v2_parent_id;   //直播父分区ID
    private String area_v2_parent_name; //直播父分区名字
    private String face;                //用户头像
    private String link;                //直播间链接
    private String online;              //直播间人数
    private String pic;                 //直播封面
    private String remark;              //标题
    private String roomid;              //直播间ID
    private String system_cover;        //直播间内容截图
    private String title;               //直播间标题 "KPL春季赛决赛 赛前表演赛"
    private String uname;               //播主用户名
    private Date created;               //获取时间
}
