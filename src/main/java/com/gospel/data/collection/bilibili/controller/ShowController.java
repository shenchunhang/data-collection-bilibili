package com.gospel.data.collection.bilibili.controller;

import com.gospel.data.collection.bilibili.CollectBilibili;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 *项目名: data-collection-bilibili
 *文件名: ShowController
 *创建者: SCH
 *创建时间:2019/5/27 13:25
 *描述: TODO
 */
@RestController
public class ShowController {
    @Autowired
    private CollectBilibili collectBilibili;

    @RequestMapping(value = "show")
    public String show() {
        return collectBilibili.collect("https://api.bilibili.com/x/web-interface/dynamic/region?callback=jquery" +
                "Callback_bili_01121761401943977&jsonp=jsonp&ps=15&rid=138&_=1558928420355");
    }
}
