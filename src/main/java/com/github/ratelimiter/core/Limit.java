package com.github.ratelimiter.core;

import com.github.ratelimiter.handler.SysDefaultOverloadHandler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhouyinyan on 17/3/1.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Limit {

    /**
     * 默认最大速率200tps
     */
    double MAX_RATE = 200.0d;

    /**
     * 最大速率（最小值不能小于0.1）
     * @return
     */
    double maxRate() default MAX_RATE ;

//       /**
     //     * 限流后是否抛出OverLoadLimitException
     //     * @return true 限流后直接抛出异常
     //     *         false 限流后默认处理
     //     *                1. 如果方法返回值类型是BaseResult,则封装限流错误的result返回
     //     *                2. 如果方法返回值类型非BaseResult，则简单粗暴的返回null
     //     *                3. 如果方法无返回类型，则直接丢弃此次调用（存在风险）
     //     */
//    boolean throwException() default false;

    /**
     * 限流处理器
     * @return
     */
    Class<?> overloadHandler() default  SysDefaultOverloadHandler.class;

    /**
     * 限流阈值是否配置在外部配置中心
     * @return
     */
    boolean withConfigCenter() default false;

    /**
     * 限流阈值在配置中心的key
     * @return
     */
    String configCenterKey() default "";
}
