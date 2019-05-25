package com.yax.messagequeue.netty;

import com.alibaba.fastjson.JSONObject;
import com.yax.messagequeue.service.LogicService;
import com.yax.messagequeue.util.VeDate;
import com.yax.redisqueue.messageModel.PushModel;
import com.yax.redisqueue.messageModel.ResponseInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author yax
 * @create 2019-04-06 15:02
 **/
@Component
@Scope("prototype")
//@ChannelHandler.Sharable
public class PushModelHandler extends SimpleChannelInboundHandler<PushModel> {
    @Autowired
    private LogicService logicService;
    private static final Logger log = LoggerFactory.getLogger(PushModelHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, PushModel pushModel) throws Exception {
        boolean isSucess=false;
        String msgId=pushModel.getMsgId();
        try {
            log.info("生产者消息: " + pushModel.toString());
            int delayType = pushModel.getDelayType();
            if (delayType == 0) {
                if (!logicService.push(pushModel)) {
                    log.info(VeDate.getStringDate() + " 推送失败: " + pushModel.toString());
                }else{
                    isSucess=true;
                }
            } else if (delayType == 1) {
            /*if(!logicService.delayPush(pushModel)){
                log.info("推送失败:"+pushModel.toString());
            }*/
                if (!logicService.delayPushZset(pushModel)) {
                    log.info(VeDate.getStringDate() + " 添加延迟队列失败(也有可能上个任务被覆盖): " + pushModel.toString());
                }else{
                    isSucess=true;
                }
            } else {
                if (!logicService.removeValueByKey(pushModel)) {
                    log.info(VeDate.getStringDate() + " 删除延迟消息失败: " + pushModel.toString());
                }else{
                    isSucess=true;
                }
            }
        }finally {
            if(pushModel.isSyn()) {
                //消息确认ack
                Channel channel = channelHandlerContext.channel();
                pushMsg(channel, new ResponseInfo(msgId, isSucess));
                log.info(msgId+" 是否接收成功 "+isSucess);
            }
        }
    }
    /*
     *推送消息
     */
    public static void pushMsg(Channel channel,Object obj){
       String json=JSONObject.toJSONString(obj)+"\n\r";
       channel.writeAndFlush(json);

    }
}
