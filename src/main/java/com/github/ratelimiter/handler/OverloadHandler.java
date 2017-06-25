package com.github.ratelimiter.handler;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 过载保护处理器，当系统出现过载的情况时，可以实现该接口自定义处理逻辑
 */
public interface OverloadHandler {

	Object handle(ProceedingJoinPoint joinPoint);

}
