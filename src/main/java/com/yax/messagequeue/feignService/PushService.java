package com.yax.messagequeue.feignService;

import com.alibaba.fastjson.JSONObject;
import com.yax.feign.annotation.FeignClient;
import com.yax.messagequeue.model.ResponseModel;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-11 17:38
 **/
@FeignClient(url="temp",fallback = PushServiceImpl.class)
public interface PushService {
    @RequestMapping("")
    ResponseModel pushCallBack(@RequestBody JSONObject data, @PathVariable(value = "temp") String url);
}
