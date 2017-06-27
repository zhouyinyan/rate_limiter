package com.github.ratelimiter.configcenter;


import com.github.ratelimiter.core.LimiterFactory;
import com.github.ratelimiter.core.exceptions.UpdateLimiterException;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouyinyan on 17/6/27.
 */
public abstract class DefaultConfigCenterClient implements ConfigCenterClient {

    /**
     * 配置中心key-value
     */
    protected Map<String,String> configCenterKeyValueMap = Maps.newConcurrentMap();

    @Autowired
    protected LimiterFactory limiterFactory;

    /**
     * 初始化configCenterKeyValueMap
     *  实现者需要自己
     */
    @PostConstruct
    protected abstract void init();

    @Override
    public String getValue(String configCenterKey) {
        return configCenterKeyValueMap.get(configCenterKey);
    }

    /**
     * 配置中心的配置值发生变化时，触发该操作
     * @param configCenterKey 配置中心key
     */
    @Override
    public void updateValue(String configCenterKey, String newValue) {

        if(configCenterKeyValueMap.containsKey(configCenterKey)){
//            configCenterKeyValueMap.replace(configCenterKey, newValue);  //this api need jdk1.8
            configCenterKeyValueMap.put(configCenterKey, newValue);
        }

        double newValueDouble = checkAndReturnNewValue(configCenterKey, newValue);

        if(limiterFactory.getConfigCenterKeyToLimiterNameMap().containsKey(configCenterKey)){
            Set<String> limiterNameSet = limiterFactory.getConfigCenterKeyToLimiterNameMap().get(configCenterKey);
            limiterFactory.updateLimiterByLimiterNameSet(limiterNameSet, newValueDouble);
        }

    }

    private double checkAndReturnNewValue(String configCenterKey, String newValue){
        try {
            return Double.valueOf(newValue);
        }catch (NumberFormatException e){
            throw new UpdateLimiterException("使用了配置中心的动态更新， 配置中心的value必须配置为数字，配置key:" + configCenterKey);
        }catch (NullPointerException e){
            throw new UpdateLimiterException("使用了配置中心的动态更新, 配置中心的value不能配置为空，配置key:" + configCenterKey);
        }
    }

    public Map<String, String> getConfigCenterKeyValueMap() {
        return configCenterKeyValueMap;
    }

    public void setConfigCenterKeyValueMap(Map<String, String> configCenterKeyValueMap) {
        this.configCenterKeyValueMap = configCenterKeyValueMap;
    }
}
