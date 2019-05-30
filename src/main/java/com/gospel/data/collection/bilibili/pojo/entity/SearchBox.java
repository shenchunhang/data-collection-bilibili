package com.gospel.data.collection.bilibili.pojo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/*
 *项目名: data-collection-bilibili
 *文件名: SearchBox
 *创建者: SCH
 *创建时间:2019/5/31 0:49
 *描述: TODO
 */
@Data
@Entity
public class SearchBox {
    @Id
    private long id;
    private long dataId;        //记录里面的ID
    private String showName;    //搜索框里面的tag
    private String name;        //av号
    private String seid;        //不知道什么东西
    private int type;           //不知道是什么类型
    private Date created;
}
