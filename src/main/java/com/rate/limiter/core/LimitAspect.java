package com.rate.limiter.core;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by zhouyinyan on 17/2/6.
 */
@Aspect
@Component
public class LimitAspect implements Ordered{

    @Autowired
    LimiterFactory limiterFactory;

    @Pointcut("@annotation(com.rate.limiter.core.Limit)")
    public void limitPointCut(){}

    @Around("limitPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        Class<?> targetClass = joinPoint.getTarget().getClass();

        //方法级别注解
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        Limit methodLimit = AnnotationUtils.findAnnotation(method, Limit.class);
        if(null != methodLimit){
            String methodLevelKey = joinPoint.getThis().getClass().getName() + method.getName();
            RateLimiter methodLimiter = limiterFactory.getLimiter(methodLevelKey);
            if (!methodLimiter.tryAcquire()) {
                return limiterFactory.getOverloadHandler(methodLevelKey).handle(joinPoint);
            }
            return joinPoint.proceed();//存在方法级别注解，忽略类级别注解
        }

        //类级别注解
        Limit classLimit = AnnotationUtils.findAnnotation(targetClass, Limit.class);
        if(null != classLimit){
            String classLevelKey = joinPoint.getThis().getClass().getName();
            RateLimiter classLimiter = limiterFactory.getLimiter(classLevelKey);
            if (!classLimiter.tryAcquire()) {
               return limiterFactory.getOverloadHandler(classLevelKey).handle(joinPoint);
            }
            return joinPoint.proceed();
        }

        return joinPoint.proceed();
    }

    /**
     * 封装限流时返回的结果
     * @param method
     * @return
     */
    private Object overLoadLimitResult(Method method) {
        Class<?> returnType =  method.getReturnType();
//        if(BaseResult.class.isAssignableFrom(returnType)){
//            BaseResult result = (BaseResult) BeanUtils.instantiateClass(returnType);
//            result.setSuccess(false);
//            result.setCode(LIMIT_ERROR_CODE);
//            result.setDescription(LIMIT_ERROR_CODE);
//            return result;
//        }
        return null;
    }


    @Override
    public int getOrder() {
        return -100;//高优先级
    }
}

