package com.yax.messagequeue.netty;


import com.yax.messagequeue.constant.Constants;
import com.yax.messagequeue.util.FileUtil;
import com.yax.messagequeue.util.RedisCacheManager;
import com.yax.messagequeue.util.VeDate;
import com.yax.redisqueue.messageModel.PushInfo;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Scope;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Scope("prototype")
//@ChannelHandler.Sharable
public class RuntasticHeartHandler extends ChannelInboundHandlerAdapter implements EnvironmentAware {
    @Autowired
    private RedisCacheManager redisCacheManager;
    private Environment environment;
    private int loss_connect_time = 0;
    public static Map<String, Queue<Channel>> channelMap=new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(RuntasticHeartHandler.class);
    public static Map<String, Queue<Channel>> getChannelMap() {
        return channelMap;
    }

    public static void setChannelMap(ConcurrentHashMap<String, Queue<Channel>> channelMap) {
        RuntasticHeartHandler.channelMap = channelMap;
    }
    public static Queue<Channel> getChannelQueueByName(String queueName){
        return channelMap.get(queueName);
    }

    //通道激活时触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
         Channel channel=ctx.channel();
         ChannelId ch= channel.id();
        SocketAddress socketAddress= channel.remoteAddress();
        if(socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            String ip=inetSocketAddress.getHostName();
            int port=inetSocketAddress.getPort();
            String openWhiteList =environment.getProperty("netty.server.openWhiteList");
            if("true".equals(openWhiteList)){
              String  white= FileUtil.readFileAsString("/whiteList.config");
              List<String> whiteList= Arrays.asList(white.split(","));
              if(!whiteList.contains(ip)){
                  channel.close();
                  logger.info("channelId " + ch.asLongText() + " 非法连接!!!" + " " + VeDate.getStringDate() + " ip:" + ip + " port:" + port);
              }else{
                  logger.info("channelId " + ch.asLongText() + " 连接成功" + " " + VeDate.getStringDate() + " ip:" + ip + " port:" + port);
              }
            }else {
                logger.info("channelId " + ch.asLongText() + " 连接成功" + " " + VeDate.getStringDate() + " ip:" + ip + " port:" + port);
            }
        }else {
            logger.info("channelId " + ch.asLongText() + " 连接成功" + " " + VeDate.getStringDate());
        }
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
                loss_connect_time++;
                if(loss_connect_time> Constants.LOSS_CONNECT_TIME){
                    Channel channel=ctx.channel();
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
                    String ip=inetSocketAddress.getHostName();
                    int port=inetSocketAddress.getPort();
                    logger.info("关闭这个不活跃通道！channelId:"+channel.id().asLongText()+"  "+VeDate.getStringDate()+" ip:"+ip+" port:"+port);
                    channel.close();
                }
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    /**
     * @param channelHandlerContext
     * @param
     * @throws Exception
     * 通道有读操作时触发
     */
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
        loss_connect_time=0;
        boolean release = true;
        try {
            if (obj instanceof PushInfo) {
                PushInfo pushInfo = (PushInfo) obj;
                int loginType = pushInfo.getLoginType();
                if (loginType == 0) {
                    logger.info("监听队列 :"+obj.toString());
                    Channel channel = channelHandlerContext.channel();
                    List<String> queueNames = pushInfo.getQueueName();
                    queueNames.forEach(queueName -> {
                        Queue<Channel> queue = channelMap.get(queueName);
                        if (queue == null) {
                            queue = new ConcurrentLinkedQueue<>();
                            queue.add(channel);
                            channelMap.put(queueName, queue);
                        } else {
                            queue.add(channel);
                        }
                        failureCompensation(queueName, channel);
                    });
                }else{
                    logger.info("心跳信息 :"+obj.toString());
                }
            } else {
                release = false;
                channelHandlerContext.fireChannelRead(obj);
            }
        }finally {
            if(release){
                ReferenceCountUtil.release(obj);
            }
        }

    }
    /*
    *channel读结束时执行的操作
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     *  长链接模式下 失败补偿
     * @param queueName
     * @param channel
     */
    private void failureCompensation(String queueName,Channel channel){
        List<Object>  data= redisCacheManager.lGet(queueName,0,-1);
        data.forEach(o -> PushModelHandler.pushMsg(channel, o));
        redisCacheManager.del(queueName);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }
}
