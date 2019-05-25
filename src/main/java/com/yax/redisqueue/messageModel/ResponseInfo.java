package com.yax.redisqueue.messageModel;

/**
 * @author yax
 * @create 2019-05-17 15:56
 **/
public class ResponseInfo {
    public String msgId;
    private boolean isSuccess;
   private ResponseInfo(){

   }
    public ResponseInfo(String msgId ,boolean isSuccess){
         this.msgId=msgId;
         this.isSuccess=isSuccess;
    }
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public static ResponseInfo success(String msgId){
        return new ResponseInfo(msgId,true);
    }
    public static ResponseInfo fail(String msgId){
        return new ResponseInfo(msgId,false);
    }
}
