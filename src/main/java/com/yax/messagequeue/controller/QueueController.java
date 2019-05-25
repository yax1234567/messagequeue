package com.yax.messagequeue.controller;

import com.yax.messagequeue.model.ResponseModel;
import com.yax.messagequeue.service.LogicService;
import com.yax.messagequeue.util.VeDate;
import com.yax.redisqueue.messageModel.PushModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-11 16:09
 **/
@RestController
@RequestMapping("queue")
public class QueueController {
    @Autowired
    private LogicService logicService;
    @RequestMapping("pushQueue")
     public ResponseModel pushQueue(@RequestBody PushModel requestModel){
        int delayType= requestModel.getDelayType();
        if(delayType==0){
            if(logicService.push(requestModel)){
                return ResponseModel.success();
            }
        }else if(delayType==1){
            /*if(logicService.delayPush(requestModel)){
                return ResponseModel.success();
            }*/
            if(logicService.delayPushZset(requestModel)){
                return ResponseModel.success();
            }
        }else{
            if(!logicService.removeValueByKey(requestModel)){
                return ResponseModel.success();
            }
        }
        return ResponseModel.fail();
     }
}
