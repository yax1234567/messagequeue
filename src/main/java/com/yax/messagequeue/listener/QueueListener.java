package com.yax.messagequeue.listener;

import com.yax.messagequeue.util.QuartzManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-11 15:44
 **/
@Component
public class QueueListener implements ApplicationListener<ContextRefreshedEvent>, EnvironmentAware {
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    private Environment environment;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        String cron= StringUtils.isEmpty(environment.getProperty("quartz.listener.cron"))?"0 */1 * * * ?":environment.getProperty("quartz.listener.cron");
        try {
            Class cla = Class.forName("com.yax.messagequeue.task.QueueTask");
            quartzManager.addJob("queueTask",cla,cron,"定时轮询队列");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }
}
