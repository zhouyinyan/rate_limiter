package com.github.ratelimiter.core;

import com.google.common.collect.Maps;
import com.github.ratelimiter.configcenter.ConfigCenterClient;

import java.util.Map;

/**
 * Created by zhouyinyan on 17/6/25.
 */
//@Component
public class TestConfigCenterClient implements ConfigCenterClient {

    //data for test
    static Map<String, String> kvMap = Maps.newConcurrentMap();

    public static final String K1 = "k1";
    public static final String K2 = "k2";
    public static final String K3 = "k3";
    public static final String K4 = "k4";
    public static final String K5 = "k5";
    public static final String K6 = "k6";
    public static final String K7 = "k7";

    static {
        kvMap.put(K1, "1");
        kvMap.put(K2, "2");
        kvMap.put(K3, "3");
        kvMap.put(K4, "4");
        kvMap.put(K5, "5");
        kvMap.put(K6, "6");
        kvMap.put(K7, "7");
    }

    @Override
    public String getValueByKey(String configCenterKey) {
        return kvMap.get(configCenterKey);
    }
}
