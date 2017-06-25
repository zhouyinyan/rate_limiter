package com.rate.limiter.core;

import org.springframework.stereotype.Service;

/**
 * Created by zhouyinyan on 17/2/6.
 */
@Service
@Limit(maxRate = 3.0) //类级别限流3，本地限流配置，默认的限流处理器。
public class DemoService {

    /**
     * 场景一
     *  使用类级别配置
     */
    public void scene1(){
        System.out.println("--场景一:使用类级别配置（类级别限流3，本地限流配置，默认的限流处理器。）--");
    }

    /**
     * 场景二
     *  方法级别配置，使用本地配置，默认的200速率，默认的限流处理器
     */
    @Limit
    public void scene2(){
        System.out.println("--场景二:方法级别配置，使用本地配置，默认的200速率，默认的限流处理器--");
    }

    /**
     * 场景三
     *  方法级别配置，使用本地配置，1TPS速率，默认的限流处理器
     */
    @Limit(maxRate = 1.0)
    public void scene3(){
        System.out.println("--场景三:方法级别配置，使用本地配置，1TPS速率，默认的限流处理器--");
    }

    /**
     * 场景四
     *  方法级别配置，使用本地配置，1TPS速率，自定义的限流处理器
     */
    @Limit(maxRate = 1.0, overloadHandler = CustomOverloadHandler.class)
    public void scene4(){
        System.out.println("--场景四:方法级别配置，使用本地配置，1TPS速率，自定义的限流处理器--");
    }

    /**
     * 场景五
     *  方法级别配置，使用配置中心，1TPS速率，默认的限流处理器
     */
    @Limit(withConfigCenter = true, configCenterKey = TestConfigCenterClient.K1)
    public void scene5(){
        System.out.println("--场景五:方法级别配置，使用配置中心，1TPS速率，默认的限流处理器--");
    }

    /**
     * 场景六
     *  方法级别配置，使用配置中心，2TPS速率，自定义的限流处理器
     */
    @Limit(withConfigCenter = true, configCenterKey = TestConfigCenterClient.K2, overloadHandler = CustomOverloadHandler.class)
    public void scene6(){
        System.out.println("--场景六:方法级别配置，使用配置中心，1TPS速率，自定义的限流处理器--");
    }
}
