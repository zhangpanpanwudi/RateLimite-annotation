package com.wonders.annotation;

import java.lang.annotation.*;

/**
 * @author: zph
 * @data: 2019/01/26 15:08
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtRateLimite {

    //以秒为单位，以固定的速率向桶中添加令牌
    double permitsPerSecond();

    //在规定的毫秒内，如果没有获取到令牌，就进行服务降级处理
    long timeout();
}
