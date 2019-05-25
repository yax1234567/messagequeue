package com.yax.messagequeue.controller;

import com.yax.messagequeue.feignService.TestService;
import com.yax.messagequeue.model.ResponseModel;
import com.yax.messagequeue.model.RuningInfo;
import com.yax.messagequeue.netty.RuntasticHeartHandler;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * ${DESCRIPTION}
 *  测试controller
 * @author yax
 * @create 2019-03-12 16:01
 **/
@RestController
@RequestMapping("test")
public class TestController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private TestService testService;
    //@Autowired
    //private PushClient pushClient;
    @RequestMapping("testFeign")
    public ResponseModel testFeign() throws IOException, ServletException {
        /*Collection<Part> parts = request.getParts();
        System.out.println("---------stream-----------");
        for (Iterator<Part> iterator = parts.iterator(); iterator.hasNext();) {
         Part part = iterator.next();
         System.out.println("-----类型名称------->"+part.getName());
         System.out.println("-----类型------->"+part.getContentType());
         System.out.println("-----提交的类型名称------->"+part.getSubmittedFileName());
         System.out.println("----流-------->"+part.getInputStream());
        }*/
        BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuffer tStringBuffer = new StringBuffer();
        String sTempOneLine ;
        System.out.println("---------body-----------");
        while ((sTempOneLine = tBufferedReader.readLine()) != null){
            tStringBuffer.append(sTempOneLine);
        }
        System.out.println(tStringBuffer.toString());
        System.out.println("---------form-----------");
        request.getParameterMap().forEach((k,v)->{
            System.out.println(k+":"+ v[0]);
        });
       System.out.println("-------hearder---------");
       Enumeration<String> enums=request.getHeaderNames();
                while(enums.hasMoreElements()){
                   String key= enums.nextElement();
                    System.out.println(key+":"+request.getHeader(key));
                }

        return ResponseModel.success();
    }
    @RequestMapping("testService")
    public ResponseModel testService(@RequestParam("image") MultipartFile file,@RequestParam("image") MultipartFile file1) throws Exception {
       return testService.testService("http://localhost:8080/test/testFeign","qrwr234533","yax",file,file1);
    }
    /*@RequestMapping("pushMessage")
    public ResponseModel pushMessage(){
        pushClient.pushMessage(TimeUnit.MINUTES, SendType.TCP_PUSH,null,1,ResponseModel.success("时间: "+ VeDate.getStringDate()),"nettyQueue");
        return ResponseModel.success();
    }*/
    @RequestMapping("isRunning")
    public ResponseModel isRunning(){
        Set<Channel> channels=new HashSet<>();
        Map<String, Queue<Channel>> channelMap= RuntasticHeartHandler.getChannelMap();
        channelMap.values().forEach(queue->queue.forEach(channel ->channels.add(channel) ));
        RuningInfo runingInfo=new RuningInfo();
        List<InetSocketAddress> inetSocketAddresses=new ArrayList<>();
        channels.forEach(channel -> {
            if(channel.isOpen()){
                runingInfo.selfIncrement();
                InetSocketAddress inetSocketAddress = (InetSocketAddress)channel.remoteAddress();
                inetSocketAddresses.add(inetSocketAddress);
            }
        });
        runingInfo.setInetSocketAddresss(inetSocketAddresses);
        return ResponseModel.success("is runing !!!",runingInfo);
    }
}
