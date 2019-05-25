package com.yax.messagequeue.netty;

import com.yax.messagequeue.CustomEncoder.ByteToPushModelDecode;
import com.yax.messagequeue.CustomEncoder.StringToByteEncode;
import com.yax.messagequeue.constant.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author yax
 * @create 2019-04-06 12:41
 **/
@Component
public class NettyServer implements EnvironmentAware , InitializingBean , ApplicationContextAware {
    private ApplicationContext applicationContext;
    private Environment environment;
    private int port;
    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        port=Integer.valueOf(environment.getProperty("netty.server.port")==null?"8081":environment.getProperty("netty.server.port"));
    }
    public void startServer() {
        /*System.setProperty("io.netty.leakDetection.maxRecords", "100");
        System.setProperty("illL'o.netty.leakDetection.acquireAndReleaseOnly", "true");
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);*/  //开启堆外内存溢出检测

        String maxLength=environment.getProperty("netty.server.maxLength");
        String bossGroupThreadCount=environment.getProperty("netty.server.bossGroupThreadCount");
        String workGroupThreadCount=environment.getProperty("netty.server.workGroupThreadCount");
        EventLoopGroup bossGroup;
        EventLoopGroup workerGroup;
        if(bossGroupThreadCount==null) {
            bossGroup = new NioEventLoopGroup();
        }else{
            bossGroup = new NioEventLoopGroup(Integer.valueOf(bossGroupThreadCount));
        }
        if(workGroupThreadCount==null) {
            workerGroup = new NioEventLoopGroup();
        }else{
            workerGroup=new NioEventLoopGroup(Integer.valueOf(workGroupThreadCount));
        }

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024) //设置tcp缓冲区(// 保持连接数  )
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //保持连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //心跳包检测
                            ch.pipeline().addLast(new IdleStateHandler(Constants.HEART_BEAT_RATE, 0, 0, TimeUnit.SECONDS));
                            //通过在数据包里末尾添加换行符来防止粘包和拆包
                            ch.pipeline().addLast(new LineBasedFrameDecoder(maxLength==null?1024:Integer.valueOf(maxLength)));
                            StringToByteEncode StringToByteEncode=applicationContext.getBean(StringToByteEncode.class);
                            ByteToPushModelDecode byteToPushModelDecode=applicationContext.getBean(ByteToPushModelDecode.class);
                            RuntasticHeartHandler runtasticHeartHandler=applicationContext.getBean(RuntasticHeartHandler.class);
                            PushModelHandler pushModelHandler=applicationContext.getBean(PushModelHandler.class);
                            ch.pipeline().addLast(StringToByteEncode);
                            ch.pipeline().addLast(byteToPushModelDecode);
                            ch.pipeline().addLast(runtasticHeartHandler);
                            ch.pipeline().addLast(pushModelHandler);
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        }
        catch (Exception e){
             e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       this.applicationContext=applicationContext;
    }
}
