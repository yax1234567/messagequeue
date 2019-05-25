package com.yax.messagequeue.model;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author yax
 * @create 2019-04-14 22:31
 **/
public class RuningInfo {
    private int onlineChannelCount;
    private List<InetSocketAddress>  inetSocketAddresss;

    public int getOnlineChannelCount() {
        return onlineChannelCount;
    }

    public void setOnlineChannelCount(int onlineChannelCount) {
        this.onlineChannelCount = onlineChannelCount;
    }

    public List<InetSocketAddress> getInetSocketAddresss() {
        return inetSocketAddresss;
    }

    public void setInetSocketAddresss(List<InetSocketAddress> inetSocketAddresss) {
        this.inetSocketAddresss = inetSocketAddresss;
    }

    public void selfIncrement(){
        onlineChannelCount++;
    }
}
