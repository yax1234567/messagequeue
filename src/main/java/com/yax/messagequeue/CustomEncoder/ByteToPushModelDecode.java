package com.yax.messagequeue.CustomEncoder;

import com.alibaba.fastjson.JSONObject;
import com.yax.redisqueue.messageModel.PushInfo;
import com.yax.redisqueue.messageModel.PushModel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Scope("prototype")
public class ByteToPushModelDecode extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

            byte[] req = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(req);
            String body = new String(req, "utf-8");
            if (body.contains("loginType")) {
                PushInfo pushInfo = JSONObject.parseObject(body, PushInfo.class);
                list.add(pushInfo);
            } else {
                PushModel pushData = JSONObject.parseObject(body, PushModel.class);
                list.add(pushData);
            }
         }
}
