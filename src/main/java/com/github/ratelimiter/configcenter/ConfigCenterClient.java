package com.github.ratelimiter.configcenter;

/**
 * 配置中心客户端
 * Created by zhouyinyan on 17/6/25.
 */
public interface ConfigCenterClient {

    /**
     * 获取配置中心配置值
     * @param configCenterKey 配置中心key
     * @return 配置值
     */
    String getValueByKey(String configCenterKey);
}
