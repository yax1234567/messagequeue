package com.yax.messagequeue.service;

import com.alibaba.fastjson.JSONObject;
import com.yax.messagequeue.feignService.PushService;
import com.yax.messagequeue.model.ResponseModel;
import com.yax.messagequeue.netty.PushModelHandler;
import com.yax.messagequeue.netty.RuntasticHeartHandler;
import com.yax.messagequeue.util.RedisCacheManager;
import com.yax.messagequeue.util.VeDate;
import com.yax.redisqueue.messageModel.MessageModel;
import com.yax.redisqueue.messageModel.PushModel;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Queue;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-11 16:40
 **/
@Service
public class LogicService {
    @Autowired
    private RedisCacheManager redisCacheManager;
    @Autowired
    private PushService pushService;
    /**
     * 立即推送
     * @param requestModel
     * @return
     */
    public boolean push(PushModel requestModel){
        try {
            String queueName = requestModel.getQueueName();
            Object data = requestModel.getData();
            int retryCount=requestModel.getRetryCount();
            MessageModel messageModel=new MessageModel(queueName,data,retryCount);
            int sendType = requestModel.getSendType();
            if (sendType == 0) {
                return redisCacheManager.rightPush(queueName, messageModel);
            }
            if (sendType == 1) {
                String pushUrl = requestModel.getPushUrl();
                ResponseModel responseModel = pushService.pushCallBack(JSONObject.parseObject(JSONObject.toJSONString(data)), pushUrl);
                if (responseModel.getCode() == 0) {
                    return true;
                }
            }
            if (sendType == 2) {
                Queue<Channel> queue = RuntasticHeartHandler.getChannelQueueByName(queueName);
                if (queue != null) {
                    for (Channel channel = queue.poll(); channel != null; channel = queue.poll()) {
                        if (channel.isOpen()) {
                            PushModelHandler.pushMsg(channel, messageModel);
                            queue.add(channel);
                            return true;
                        }
                    }
                }
                return redisCacheManager.rightPush(queueName, messageModel);
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 延迟推送
     * @param requestModel
     * @return
     */
    public boolean delayPush(PushModel requestModel){
        String expectedTime=requestModel.getExpectedTime();
        long delaySeconds=0;
        if(StringUtils.isEmpty(expectedTime)){
            int timeUnit=requestModel.getTimeUnit();
            int time=requestModel.getDelayTime();
            if(timeUnit==0){
                delaySeconds=time*60L;
              long timeM=System.currentTimeMillis()+delaySeconds*1000L;
                expectedTime= VeDate.stampToDate(timeM);
            }
            if(timeUnit==1){
                delaySeconds=time*60L*60L;
                long timeM=System.currentTimeMillis()+delaySeconds*1000L;
                expectedTime= VeDate.stampToDate(timeM);
            }
            if(timeUnit==2){
                delaySeconds=time*60L*60L*24L;
                long timeM=System.currentTimeMillis()+delaySeconds*1000L;
                expectedTime= VeDate.stampToDate(timeM);
            }

        }else{
            Date date= VeDate.strToDateLongT(expectedTime);
            delaySeconds=(date.getTime()-System.currentTimeMillis())/1000;
        }
        delaySeconds=delaySeconds+60L*60L;
        return redisCacheManager.rightPush(expectedTime,requestModel,delaySeconds);
    }
    /**
     * 延迟推送 (zset版本)
     * @param requestModel
     * @return
     */
    public boolean delayPushZset(PushModel requestModel){
        String expectedTime=requestModel.getExpectedTime();
        long timeStamp=0;
        long delaySeconds;
        if(StringUtils.isEmpty(expectedTime)){
            int timeUnit=requestModel.getTimeUnit();
            int time=requestModel.getDelayTime();
            if(timeUnit==0){
                delaySeconds=time*60L;
                timeStamp=System.currentTimeMillis()+delaySeconds*1000L;
            }
            if(timeUnit==1){
                delaySeconds=time*60L*60L;
                timeStamp=System.currentTimeMillis()+delaySeconds*1000L;
            }
            if(timeUnit==2){
                delaySeconds=time*60L*60L*24L;
                timeStamp=System.currentTimeMillis()+delaySeconds*1000L;
            }
        }else{
            Date date= VeDate.strToDateLongT(expectedTime);
            timeStamp=date.getTime();
        }
        return redisCacheManager.zaddSet("redisDelayQueue",requestModel,timeStamp);
    }
    public boolean removeValueByKey(PushModel pushModel){
        pushModel.setDelayType(1);
       return redisCacheManager.removeZsetByKeyValue("redisDelayQueue",pushModel);
    }
}
