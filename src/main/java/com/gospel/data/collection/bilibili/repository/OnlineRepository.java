package com.gospel.data.collection.bilibili.repository;

import com.gospel.data.collection.bilibili.pojo.entity.Online;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/*
 *项目名: data-collection-bilibili
 *文件名: OnlineRepository
 *创建者: SCH
 *创建时间:2019/5/29 13:39
 *描述: TODO
 */
public interface OnlineRepository extends JpaRepository<Online, Long> {
    //根据编号查询
//    @Query(value = "select * from online where year = %?1% and month = %?2%", nativeQuery = true)
//    @Modifying
//    List<Online> findByYearAndMouth(Date year, int month);

}
