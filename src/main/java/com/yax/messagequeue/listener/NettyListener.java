package com.yax.messagequeue.listener;

import com.yax.messagequeue.netty.NettyServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yax
 * @create 2019-04-06 12:16
 **/
@Component
public class NettyListener implements ApplicationListener<ContextRefreshedEvent> , DisposableBean {
    @Autowired
    private NettyServer nettyServer;
    private ExecutorService startGroup= Executors.newSingleThreadExecutor();
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        startGroup.execute(() -> nettyServer.startServer());
    }

    @Override
    public void destroy() throws Exception {
        startGroup.shutdownNow();
    }
}
