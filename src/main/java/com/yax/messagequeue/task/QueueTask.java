package com.yax.messagequeue.task;

import com.yax.messagequeue.service.LogicService;
import com.yax.messagequeue.util.RedisCacheManager;
import com.yax.messagequeue.util.VeDate;
import com.yax.redisqueue.messageModel.PushModel;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-11 15:49
 **/
@Component
@DisallowConcurrentExecution
public class QueueTask implements Job {
    @Autowired
    private RedisCacheManager redisCacheManager;
    @Autowired
    private LogicService logicService;
   /* @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
       String shortDay= VeDate.getStringDateNs();
       List<Object> data= redisCacheManager.lGet(shortDay,0,-1);
       data.forEach(obj->{
           PushModel requestModel= (PushModel) obj;
           if(!logicService.push(requestModel)){
               //异常处理
           }
       });
        redisCacheManager.del(shortDay);
    }*/
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        long timeStamp=System.currentTimeMillis();
        Set<Object> data=redisCacheManager.zRangeByScore("redisDelayQueue",0,timeStamp);
        redisCacheManager.removeRangeByScore("redisDelayQueue",0,timeStamp);
        data.forEach(obj->{
            if(!logicService.push((PushModel) obj)){
                //异常处理
            }
        });
    }
}
