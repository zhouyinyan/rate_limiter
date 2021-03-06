package com.github.ratelimiter.core;

import com.github.ratelimiter.configcenter.ConfigCenterClient;
import com.github.ratelimiter.handler.OverloadHandler;
import com.github.ratelimiter.handler.SysDefaultOverloadHandler;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.RateLimiter;
import com.github.ratelimiter.core.exceptions.InitLimiterException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhouyinyan on 17/3/2.
 */
@Component
public class LimiterFactory implements InitializingBean {

    public static final String APP_DEFAULT_OVERLOAD_HANDLER_NAME = "APP_DEFAULT_OVERLOAD_HANDLER_NAME";

    /**
     * 最小速率限制
     */
    public static final double MIN_RATE = 0.1d;


    /**
     * 限流器map， 包含
     * 1. 类级别限流器 <类名称, 类级别限流器>
     * 2. 方法级别限流器 <类名称+方法名称, 方法级别限流器>
     */
    private Map<String, RateLimiter> limiterMap = Maps.newConcurrentMap();

    /**
     * 限流处理器map， 包含
     * 1. 类级别限流处理 <类名称, 类级别限流处理器类型>
     * 2. 方法级别限流处理 <类名称+方法名称, 方法级别限流处理器类型>
     *
     * 注意:
     *  1. OverloadHandler需注册为SpringBean，否则注册不上，就会采用默认的处理器。
     *  2. OverloadHandler需注意多线程同步问题，因为会有多个线程共享同一个处理器。
     */
    private Map<String, OverloadHandler> overloadHandlerMap = Maps.newConcurrentMap();

    /**
     * 应用默认限流处理器
     *    需要注册为springbean，且名字固定为"APP_DEFAULT_OVERLOAD_HANDLER_NAME"
     */
    OverloadHandler appDefaultOverloadHandler;

    /**
     * 系统默认限流处理器
     */
    OverloadHandler sysDefaultOverloadHandler  =  new SysDefaultOverloadHandler();

    @Autowired
    ApplicationContext context;



    @Autowired(required = false)
    ConfigCenterClient configCenterClient;

    /**
     * 是否使用配置中心
     */
    AtomicBoolean withConfigCenterClient = new AtomicBoolean(false);


    /**
     * 配置中心key->限流器名称列表 map
     *      value设计为Set的原因是：不同的limit注解可以复用配置中心单一配置
     */
    private Map<String, Set<String>> configCenterKeyToLimiterNameMap = Maps.newConcurrentMap();


    /**
     * 扫描spring容器中的所有bean，
     *      如果类级别有limit注解，这注册类级别共享的ratelimiter；
     *      如果方法级别有limit注解，则注册方法级别独享的retelimiter；
     *      如果类级别和方法级别都存在limit注解，则方法级别优先，独享方法级别ratelimiter，剩余为注解limit的方法共享类级别retelimiter
     *
     *      限流处理器注册同理
     *
     * @throws InitLimiterException 如果初始化limiter时存在异常，则抛出InitLimiterException异常，spring容器初始化失败
     */
    @Override
    public void afterPropertiesSet() throws InitLimiterException {
        //是否配置了ConfigCenterClient 的spring bean
        try {
            context.getBean(ConfigCenterClient.class);
            withConfigCenterClient.set(true);
        } catch (BeansException e) {
            withConfigCenterClient.set(false);
        }


        Map<String, Object> allSpringBean =  context.getBeansOfType(Object.class);

        for(Map.Entry<String, Object> entry : allSpringBean.entrySet()){
            Class<?> beanClass = entry.getValue().getClass();
            Limit classLimit = AnnotationUtils.findAnnotation(beanClass, Limit.class);

            //类级别
            if(null != classLimit) {

                String limiterName = beanClass.getName();

                double maxRate = checkAndReturnRate(entry.getKey(),null,  classLimit, limiterName);
                //注册类级别共享limiter
                RateLimiter limiter = RateLimiter.create(maxRate);
                limiterMap.put(limiterName, limiter);

                //注册类级别overloadHandler
                Class clazz = classLimit.overloadHandler();
                if (!OverloadHandler.class.isAssignableFrom(clazz)){
                    throw new InitLimiterException("OverloadHandler必须是com.rate.limiter.handler.OverloadHandler类型，在类" + entry.getKey()+ "中");
                }

                if(!StringUtils.equals(clazz.getName(), SysDefaultOverloadHandler.class.getName())) {  //默认的不用注册到map中
                    OverloadHandler overloadHandler = (OverloadHandler) context.getBean(clazz);
                    overloadHandlerMap.put(beanClass.getName(), overloadHandler);
                }
            }

            //方法级别
            Method[] methods = beanClass.getMethods();
            for(Method method : methods){
                Limit methodLimit = AnnotationUtils.findAnnotation(method, Limit.class);
                if(null != methodLimit) {

                    String limiterName = beanClass.getName() + method.getName();

                    double maxRate = checkAndReturnRate(entry.getKey(), method.getName(), methodLimit, limiterName);
                    //注册方法级别limiter
                    RateLimiter methodLimiter = RateLimiter.create(maxRate);
                    limiterMap.put(limiterName, methodLimiter);

                    //注册方法级别overloadHandler
                    Class clazz = methodLimit.overloadHandler();
                    if (!OverloadHandler.class.isAssignableFrom(clazz)){
                        throw new InitLimiterException("OverloadHandler必须是com.rate.limiter.handler.OverloadHandler类型，在类" + entry.getKey()+ "中");
                    }

                    if(!StringUtils.equals(clazz.getName(), SysDefaultOverloadHandler.class.getName())) {  //默认的不用注册到map中
                        OverloadHandler overloadHandler = (OverloadHandler) context.getBean(clazz);
                        overloadHandlerMap.put(beanClass.getName() + method.getName(), overloadHandler);
                    }
                }
            }
        }

        //设置app默认处理器。
        if(context.containsBean(APP_DEFAULT_OVERLOAD_HANDLER_NAME)) {
            OverloadHandler appDefaultHandler = (OverloadHandler) context.getBean(APP_DEFAULT_OVERLOAD_HANDLER_NAME);
            this.appDefaultOverloadHandler = appDefaultHandler;
        }
    }

    /**
     * 获取limiter实例
     * @param key 类级别为类名，方法级别为类名+方法名
     * @return null 如果不存在返回null
     */
    public RateLimiter getLimiter(String key){
        return limiterMap.get(key);
    }

    /**
     * 获取OverloadHandler实例
     * @param key 类级别为类名，方法级别为类名+方法名
     * @return  优先返回注解级别处理器
     *             如无注解，则返回应用全局配置的默认处理器，
     *             如无应用全局配置的默认处理器，则返回系统默认限流处理器。
     */
    public OverloadHandler getOverloadHandler(String key){
        OverloadHandler handler = overloadHandlerMap.get(key);
        if(null == handler){
            handler = appDefaultOverloadHandler;
        }
        if(null == handler){
            handler = sysDefaultOverloadHandler;
        }
        return handler;
    }


    /**
     * 如果限流配置在配置中心，则配置中心优先
     * 如果没有配置中心， 则使用本地配置
     * @param className
     * @param methodName
     * @param limit
     * @param limiterName
     * @return
     */
    private double checkAndReturnRate(String className, String methodName, Limit limit, String limiterName) {
        double maxRate = 200.0d; //默认200,理论上配置错误会抛出异常，兜底配置

        String withClassMethodMsg = "在类[" + className+ "]中，" + ( StringUtils.isBlank(methodName) ? "" : "在方法[" + methodName + "]中" );

        //配置合法性校验
        if(limit.withConfigCenter()){

            //没有配置ConfigCenterClient
            if(!withConfigCenterClient.get()){
                throw new InitLimiterException(withClassMethodMsg + "使用了配置中心的注解，但spring容器中无ConfigCenterClient类型的Bean");
            }

            //配置在配置中心
            if(StringUtils.isBlank(limit.configCenterKey())){
                throw new InitLimiterException(withClassMethodMsg + "使用了配置中心的注解, configCenterKey必须配置");
            }

            try {
                maxRate = getMaxRateFromConfigCenter( limit.configCenterKey());
            }catch (NumberFormatException e){
                throw new InitLimiterException(withClassMethodMsg + "使用了配置中心的注解, 配置中心的value必须配置为数字，配置key:" + limit.configCenterKey());
            }catch (NullPointerException e){
                throw new InitLimiterException(withClassMethodMsg + "使用了配置中心的注解, 配置中心的value不能配置为空，配置key:" + limit.configCenterKey());
            }

            if (maxRate <= MIN_RATE) {
                throw new InitLimiterException(withClassMethodMsg + "使用了配置中心的注解, 配置中心配置的速率限制不能小于" + MIN_RATE);
            }

            //注册配置中心key 到 限流器名称的对应关系
            Set<String> limiterNameSet = configCenterKeyToLimiterNameMap.get(limit.configCenterKey());
            if(null == limiterNameSet){
                limiterNameSet = Sets.newConcurrentHashSet();
                limiterNameSet.add(limiterName);
                configCenterKeyToLimiterNameMap.put(limit.configCenterKey(), limiterNameSet);
            }else{
                limiterNameSet.add(limiterName);
            }

        }else {
            //注解配置
            if (limit.maxRate() <= MIN_RATE) {
                throw new InitLimiterException(withClassMethodMsg +"使用了注解配置, 配置的速率限制不能小于" + MIN_RATE);
            }

            maxRate = limit.maxRate();

        }
        return maxRate;
    }



    /**
     * 遍历更新每个限流器实例
     * @param limiterNameSet
     * @param newValue
     */
    public void updateLimiterByLimiterNameSet(Set<String> limiterNameSet, double newValue) {
        for (String limiterName : limiterNameSet){
            updateLimiterByLimiterName(limiterName, newValue);
        }
    }

    /**
     * 更新单个限流器实例
     * @param limiterName
     * @param newValue
     */
    public void updateLimiterByLimiterName(String limiterName, double newValue) {
        if(limiterMap.containsKey(limiterName)){
            limiterMap.get(limiterName).setRate(newValue);
        }
    }

    /**
     * 从配置中心获取对应配置值
     * @param configKey 配置中心KEY
     * @return
     */
    private double getMaxRateFromConfigCenter(String configKey) {
        String configValue = configCenterClient.getValue(configKey);
        return Double.valueOf(configValue);
    }

    public Map<String, Set<String>> getConfigCenterKeyToLimiterNameMap() {
        return configCenterKeyToLimiterNameMap;
    }

    @PreDestroy
    public void destroy(){
        limiterMap.clear();
    }


}
