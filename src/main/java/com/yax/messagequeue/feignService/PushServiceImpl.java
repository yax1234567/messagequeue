package com.yax.messagequeue.feignService;

import com.alibaba.fastjson.JSONObject;
import com.yax.messagequeue.model.ResponseModel;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-11 17:42
 **/
@Service
public class PushServiceImpl implements PushService {
    @Override
    public ResponseModel pushCallBack(JSONObject data, String url) {
        return null;
    }
}
