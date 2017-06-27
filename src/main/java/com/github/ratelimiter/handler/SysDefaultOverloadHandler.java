package com.github.ratelimiter.handler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统默认限流处理器，兜底限流处理。
 * 	建议用户实现自己的处理器，而不是直接使用系统默认的。
 * Created by zhouyinyan
 */
public class SysDefaultOverloadHandler implements OverloadHandler {

    Logger logger = LoggerFactory.getLogger(getClass().getName());

    /**
     * 系统默认处理器实现,仅仅打印错误日志。返回null
     *
     * @param joinPoint
     */
    @Override
    public Object handle(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getThis().getClass().getName();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName  = methodSignature.getMethod().getName();
        Object[] args = joinPoint.getArgs();
        StringBuffer sb = new StringBuffer(LIMIT_ERROR_CODE)
                .append("  触发限流:在类[")
                .append(className)
                .append("]的方法[")
                .append(methodName)
                .append("]，参数为:");

        for(Object o : args){
            sb.append(o.toString())
                .append(";");
        }

        String errorMsg = sb.toString();
        logger.error(errorMsg);
        return null;
    }
}
