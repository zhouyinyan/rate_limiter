package com.github.ratelimiter.core;

import com.github.ratelimiter.configcenter.DefaultConfigCenterClient;
import com.google.common.collect.Maps;
import com.github.ratelimiter.configcenter.ConfigCenterClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by zhouyinyan on 17/6/25.
 */
@Component
public class TestConfigCenterClient extends DefaultConfigCenterClient {

    public static final String K1 = "k1";
    public static final String K2 = "k2";
    public static final String K3 = "k3";
    public static final String K4 = "k4";
    public static final String K5 = "k5";
    public static final String K6 = "k6";
    public static final String K7 = "k7";


    @Override
    protected void init() {

        configCenterKeyValueMap.put(K1, "1");
        configCenterKeyValueMap.put(K2, "2");
        configCenterKeyValueMap.put(K3, "3");
        configCenterKeyValueMap.put(K4, "4");
        configCenterKeyValueMap.put(K5, "5");
        configCenterKeyValueMap.put(K6, "6");
        configCenterKeyValueMap.put(K7, "7");
    }
}
