package com.gospel.data.collection.bilibili;

/*
 *项目名: data-collection-bilibili
 *文件名: RidEnum
 *创建者: SCH
 *创建时间:2019/5/28 22:01
 *描述: TODO
 */
public enum RidEnum {
    MMD3D(1,"MMD·3D","主页-动画"),
    连载动画(13,"连载动画","主页-番剧动态"),
    国产原创相关(168,"国产原创相关","主页-国产原创相关"),
    ;
    private int code;
    private String name;
    private String part;
    RidEnum(int code, String name,String part){
        this.code = code;
        this.name = name;
        this.part = part;
    }
}
