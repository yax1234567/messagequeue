package com.yax.messagequeue.CustomEncoder;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class StringToByteEncode extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String json, ByteBuf byteBuf) throws Exception {
        //防止tcp粘包，拆包
        String jsonData= json+"\n\r";
        byte[] b=jsonData.getBytes();
        byteBuf.writeBytes(b);
    }
}
