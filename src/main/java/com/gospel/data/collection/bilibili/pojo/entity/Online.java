package com.gospel.data.collection.bilibili.pojo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/*
 *项目名: data-collection-bilibili
 *文件名: online;//
 *创建者: SCH
 *创建时间:2019/5/29 13:27
 *描述: TODO
 */
@Data
@Entity
public class Online {
    @Id
    private long id;
    private int webOnline;//网页在线人数
    private int playOnline;//手机在线人数
    private int allCount;//投稿数
    private int animaCount;//动画区投稿数
    private int dramaCount;//番剧区投稿数
    private int guochuangCount;//国创区投稿数
    private int musicCount;//音乐区投稿数
    private int danceCount;//舞蹈区投稿数
    private int ganmeCount;//游戏区投稿数
    private int scienceCount;//科技区投稿数
    private int digitalCount;//数码区投稿数
    private int lifeCount;//生活区投稿数
    private int guichuCount;//鬼畜区投稿数
    private int fashionCount;//时尚区投稿数
    private int adCount;//广告区投稿数
    private int varietyCount;//娱乐区投稿数
    private int movieCount;//影视区投稿数
    private int projectionCount;//放映厅区投稿数
    private Date created;//获取时间
    private int year;//获取年份
    private int month;//获取月份
    private int day;//获取号数
}
