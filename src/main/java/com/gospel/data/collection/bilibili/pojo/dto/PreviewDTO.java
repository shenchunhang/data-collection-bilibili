package com.gospel.data.collection.bilibili.pojo.dto;

import lombok.Data;

/*
 *项目名: data-collection-bilibili
 *文件名: PreviewDTO
 *创建者: SCH
 *创建时间:2019/6/2 13:49
 *描述: api: https://api.live.bilibili.com/room/v1/RoomRecommend/biliIndexRecList
 *      service: com.gospel.data.collection.bilibili.service.RoomRecommendThread
 *      description: 主页-正在直播-为你推荐
 */
@Data
public class PreviewDTO {
    private String pic;     //宣传图链接
    private double remark;  //备注
    private String title;   //标题
    private String url;     //活动链接
}
