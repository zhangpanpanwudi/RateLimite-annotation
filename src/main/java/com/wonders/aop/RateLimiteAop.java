package com.wonders.aop;

import com.google.common.util.concurrent.RateLimiter;
import com.wonders.annotation.ExtRateLimite;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author: zph
 * @data: 2019/01/26 15:11
 */
@Aspect
@Component
public class RateLimiteAop {

    //用于存放限流的对象，key为方法访问地址url,value为限流对象
    private ConcurrentHashMap<String,RateLimiter> rateLimiterConcurrentHashMap =new ConcurrentHashMap<>();

    @Pointcut("execution(public * com.wonders.controller.*.*(..))")
    public void rlAop(){
    }

    @Around("rlAop()")
    public Object doBefore(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //获取aop拦截的方法
        Method sinatureMethod = getSinatureMethod(proceedingJoinPoint);
        if(sinatureMethod==null){
            //报错
            return null;
        }
        //获取方法上是否有限流注解
        ExtRateLimite annotation = sinatureMethod.getAnnotation(ExtRateLimite.class);
        if(annotation==null){
            return proceedingJoinPoint.proceed();
        }
        //获取到注解属性
        double perSecond = annotation.permitsPerSecond();
        long timeout = annotation.timeout();
        //获取当前请求的url地址
        String url = getRequestURI();
        RateLimiter rateLimiter =null;
        //获取限流对象，每个url对应一个相同的限流对象
        if(rateLimiterConcurrentHashMap.containsKey(url)){
            rateLimiter = rateLimiterConcurrentHashMap.get(url);
        }else{
            //该方法第一次访问，创建限流对象
            rateLimiter = RateLimiter.create(perSecond);
            rateLimiterConcurrentHashMap.put(url,rateLimiter);
        }
        //申请令牌
        boolean b = rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        //如果没有申请到令牌，
        if(!b){
               fallback();
               return null;
        }

        return proceedingJoinPoint.proceed();
    }

    //服务降级处理方法，
    private void fallback() throws IOException {
        System.out.println("服务降级别抢了， 在抢也是一直等待的， 还是放弃吧！！！");
        // 在AOP编程中获取响应
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        try {
            writer.println("别抢了， 再抢也是一直等待的， 还是放弃吧！！！");
        } catch (Exception e) {

        } finally {
            writer.close();

        }

    }


    //获取当前请求的url地址。。。。
    private String getRequestURI() {
        return getRequest().getRequestURI();
    }

    //获取到当前request对象
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    /**
     * 获取aop拦截的方法
     * @param proceedingJoinPoint
     * @return
     */
    private Method getSinatureMethod(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        return signature.getMethod();
    }

}
