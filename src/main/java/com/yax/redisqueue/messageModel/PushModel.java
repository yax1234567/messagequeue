package com.yax.redisqueue.messageModel;

import com.alibaba.fastjson.JSONObject;
import com.yax.messagequeue.util.IdGen;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * @author yax
 * @create 2019-04-01 14:53
 **/
public class PushModel<T> implements Serializable {
    private int timeUnit;
    private int delayType;
    private int sendType;
    private String pushUrl;
    private int delayTime;
    private T data;
    private String queueName;
    private String expectedTime;
    //重试次数
    private int retryCount;
    private boolean isSyn;
    private String msgId= String.valueOf(IdGen.get().nextId());

    public PushModel() {
    }

    public PushModel(int timeUnit, int delayType, int sendType, String pushUrl, int delayTime, T data, String queueName, String expectedTime) {
        this.timeUnit = timeUnit;
        this.delayType = delayType;
        this.sendType = sendType;
        this.pushUrl = pushUrl;
        this.delayTime = delayTime;
        this.data = data;
        this.queueName = queueName;
        this.expectedTime = expectedTime;
    }

    public boolean isSyn() {
        return isSyn;
    }

    public void setSyn(boolean syn) {
        isSyn = syn;
    }

    public int getTimeUnit() {
        return this.timeUnit;
    }

    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getDelayType() {
        return this.delayType;
    }

    public void setDelayType(int delayType) {
        this.delayType = delayType;
    }

    public int getSendType() {
        return this.sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public String getPushUrl() {
        return this.pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public int getDelayTime() {
        return this.delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getQueueName() {
        return this.queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getExpectedTime() {
        return this.expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        if(!StringUtils.isEmpty(msgId)) {
            this.msgId = msgId;
        }
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    public void selfIncrement(){
        ++retryCount;
    }
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

