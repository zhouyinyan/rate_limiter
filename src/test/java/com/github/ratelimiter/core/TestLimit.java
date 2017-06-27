package com.github.ratelimiter.core;

import com.github.ratelimiter.configcenter.ConfigCenterClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouyinyan on 17/3/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LimitConfig.class})
public class TestLimit {

    @Autowired
    DemoService demoService;

    @Test
    public void testScene1(){
        demoService.scene1();
    }


    @Test
    public void testConcurrentScene1() throws InterruptedException {

        final CountDownLatch latch  = new CountDownLatch(1);

        int num = 10;
        for(int i = 0; i < num; ++i){
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                        TimeUnit.MILLISECONDS.sleep(500 * finalI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    demoService.scene1();
                }
            }, "thead:"+i).start();
        }

        latch.countDown();

        TimeUnit.SECONDS.sleep(5);


    }



    @Test
    public void testConcurrentScene2() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);

        int num = 10;
        for (int i = 0; i < num; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                        TimeUnit.MILLISECONDS.sleep(500 * finalI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    demoService.scene2();
                }
            }, "thead:" + i).start();
        }

        latch.countDown();

        TimeUnit.SECONDS.sleep(6);
    }

    @Test
    public void testConcurrentScene3() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);

        int num = 10;
        for (int i = 0; i < num; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                        TimeUnit.MILLISECONDS.sleep(100 * finalI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    demoService.scene3();
                }
            }, "thead:" + i).start();
        }

        latch.countDown();

        TimeUnit.SECONDS.sleep(6);
    }


    @Test
    public void testConcurrentScene4() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);

        int num = 10;
        for (int i = 0; i < num; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                        TimeUnit.MILLISECONDS.sleep(100 * finalI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    demoService.scene4();
                }
            }, "thead:" + i).start();
        }

        latch.countDown();

        TimeUnit.SECONDS.sleep(6);
    }



    @Test
    public void testConcurrentScene6() throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(1);

        int num = 10;
        for (int i = 0; i < num; ++i) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                        TimeUnit.MILLISECONDS.sleep(100 * finalI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    demoService.scene6();
                }
            }, "thead:" + i).start();
        }

        latch.countDown();

        TimeUnit.SECONDS.sleep(6);
    }


    @Autowired
    ConfigCenterClient configCenterClient;

    @Test
    public void testConcurrentScene7() throws InterruptedException {

        testConcurrentScene6();

        configCenterClient.updateValue(TestConfigCenterClient.K2, "3");

        testConcurrentScene6();

    }

    //异常测试
}
