package com.wonders.controller;

import com.wonders.annotation.ExtRateLimite;
import com.wonders.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zph
 * @data: 2019/01/26 15:40
 */
@RestController
public class IndexController {

    @Autowired
    private IndexService indexService;

    // 以每秒添加1个令牌到令牌桶中
    @ExtRateLimite(permitsPerSecond = 1.0, timeout = 500)
    @RequestMapping("/findIndex")
    public String findIndex() {
        System.out.println("findIndex" + System.currentTimeMillis());
        return "findIndex" + System.currentTimeMillis();
    }

}
