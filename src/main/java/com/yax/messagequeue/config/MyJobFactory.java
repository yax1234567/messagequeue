package com.yax.messagequeue.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * @author yax
 * @create 2019-04-02 13:23
 * 自定义job工厂 实现quartz 的j对象容器 和spring ioc 容器的整合
 **/
@Component
public class MyJobFactory extends AdaptableJobFactory {
    /*@Autowired
    private AutowireCapableBeanFactory capableBeanFactory;*/
    @Autowired
    private ApplicationContext applicationContext;
    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        //调用父类的方法
       // Object jobInstance = super.createJobInstance(bundle);
       // capableBeanFactory.autowireBean(jobInstance);
        Class<?> jobClass = bundle.getJobDetail().getJobClass();
        return   applicationContext.getBean(jobClass);
    }
}
