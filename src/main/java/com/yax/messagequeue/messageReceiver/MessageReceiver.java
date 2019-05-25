package com.yax.messagequeue.messageReceiver;

import com.yax.messagequeue.model.ResponseModel;
import org.springframework.stereotype.Component;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-27 17:51
 **/
@Component
public class MessageReceiver {
    /**接收消息的方法*/
    public void receiveMessage(String queueName, ResponseModel message)
    {
        System.out.println(Thread.currentThread().getName()+" : "+queueName+" : "+message);
        int a=6/0;
    }

}
