package com.yax.messagequeue.model;

import java.io.Serializable;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-11 16:10
 **/
public class ResponseModel<T>  implements Serializable {
    private int code;
    private String msg;
    private T data;

    public ResponseModel() {
    }

    public ResponseModel(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = (T) data;
    }
    public static ResponseModel<?> success(Object object) {
        return new ResponseModel(0, null, object);
    }
    public static ResponseModel<?> success() {
        return success(null);
    }
    public static ResponseModel<?> success(String msg,Object object) {
        return new ResponseModel(0, msg, object);
    }
    public static ResponseModel<?> error(int code, String msg) {
        return new ResponseModel(code, msg, null);
    }

    public static ResponseModel<?> error(String msg) {
        return error(2, msg);
    }

    public static ResponseModel<?> fail(String msg) {
        return new ResponseModel(1, msg, null);
    }
    public static ResponseModel<?> fail(String msg,Object data) {
        return new ResponseModel(1, msg, data);
    }

    public static ResponseModel<?> fail() {
        return new ResponseModel(1, null, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseModel{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
