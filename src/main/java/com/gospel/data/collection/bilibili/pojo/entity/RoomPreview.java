package com.gospel.data.collection.bilibili.pojo.entity;

import lombok.Data;

import java.util.Date;

/*
 *项目名: data-collection-bilibili
 *文件名: RoomPreview
 *创建者: SCH
 *创建时间:2019/6/3 17:14
 *描述: TODO
 */
@Data
public class RoomPreview {
    private String pic;     //宣传图链接
    private String remark;  //备注
    private String title;   //标题
    private String url;     //活动链接
    private Date created;   //获取时间
}
