package com.gospel.data.collection.bilibili.pojo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/*
 *项目名: data-collection-bilibili
 *文件名: RoomRecOnline
 *创建者: SCH
 *创建时间:2019/6/2 17:15
 *描述: 直播的人数实时
 */
@Data
@Entity
public class RoomOnline {
    @Id
    private int id;                 //id
    private int dynamic;            //动态条数
    private int onlineTotal;        //直播间个数
    private String link;            //热度链接
    private String text;            //热度标签
    private Date created;           //获取日期
}
