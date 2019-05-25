package com.yax.messagequeue.feignService;

import com.yax.feign.annotation.FeignClient;
import com.yax.feign.annotation.Header;
import com.yax.messagequeue.model.ResponseModel;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-12 16:08
 **/
@FeignClient(url="")
@Service
public interface TestService {
    @RequestMapping("path")
    ResponseModel testService(@PathVariable(value="path") String path, @Header(key="token") String token, @Header(key="userId") String userId, @RequestParam("image") MultipartFile file,@RequestParam("image") MultipartFile file1);
}
