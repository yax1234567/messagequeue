package com.yax.redisqueue.messageModel;

/**
 * @author yax
 * @create 2019-04-11 20:05
 **/
public class MessageModel {
    private String queueName;
    private Object data;
    private int retryCount;

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public MessageModel() {
    }

    public MessageModel(String queueName, Object data,int retryCount) {
        this.queueName = queueName;
        this.data = data;
        this.retryCount=retryCount;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    public void selfIncrement(){
        ++retryCount;
    }
}
