package com.github.ratelimiter.core;

import com.github.ratelimiter.handler.OverloadHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 测试自定义限流处理器。
 * Created by zhouyinyan
 */
@Component
public class CustomOverloadHandler implements OverloadHandler {

    Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    public Object handle(ProceedingJoinPoint joinPoint) {
        logger.error(Thread.currentThread().getName() + " : OVERLOAD_ERROR IN CLASS: " + joinPoint.getThis().getClass().getName());
        return null;
    }
}
