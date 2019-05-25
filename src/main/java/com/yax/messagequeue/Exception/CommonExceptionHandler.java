package com.yax.messagequeue.Exception;

import com.yax.messagequeue.model.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class CommonExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(CommonExceptionHandler.class);

    /**
     *  拦截Exception类的异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseModel exceptionHandler(Exception e){
        log.error(printThrowable(e));
        return ResponseModel.fail("网络异常，请重试！");
    }

    private  String printThrowable(Throwable e){
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw, true);
         e.printStackTrace(pw);
         pw.flush();
         sw.flush();
        return sw.toString();
}
}
