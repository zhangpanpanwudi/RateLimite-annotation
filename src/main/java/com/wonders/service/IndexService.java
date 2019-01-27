package com.wonders.service;

import org.springframework.stereotype.Service;

/**
 * @author: zph
 * @data: 2019/01/26 15:40
 */
@Service
public class IndexService {

    public boolean addOrder() {
        System.out.println("db....正在操作订单表数据库...");
        return true;
    }

}
