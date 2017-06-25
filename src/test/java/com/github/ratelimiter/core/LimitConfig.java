package com.github.ratelimiter.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by zhouyinyan on 17/2/6.
 */
@Configuration
@ComponentScan(basePackageClasses = {LimitConfig.class, LimiterFactory.class})
@EnableAspectJAutoProxy
public class LimitConfig {
}
