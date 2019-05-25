package com.yax.messagequeue.constant;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-14 10:27
 **/
public class Constants {
    //心跳检测频率
    public static final long HEART_BEAT_RATE=120;

    public static final int LOSS_CONNECT_TIME=3;
    //推送目标url地址配置
    public final static String messageQueueUrl="http://localhost:8080/queue/pushQueue";


}
