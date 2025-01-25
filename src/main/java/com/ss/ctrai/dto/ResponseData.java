package com.ss.ctrai.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ResponseData<T> implements Serializable {
    private T data;
    private String msg;
    private Integer code = 200;

    public static final int SUCCESS = 200;
    public static final int FAIL_401 = 401;
    public static final int FAIL_400 = 400;
    public static final int FAIL_500 = 500;
    public static final int FAIL_503 = 503;

    public static <T> ResponseData<T> success(T data) {
        ResponseData<T> response = new ResponseData<>();
        response.setCode(SUCCESS);
        response.setData(data);
        response.setMsg("success");
        return response;
    }

    public static <T> ResponseData<T> fail(int code, String message) {
        ResponseData<T> response = new ResponseData<>();
        response.setCode(code);
        response.setMsg(message);
        return response;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "data=" + data +
                ", msg='" + msg + '\'' +
                ", code=" + code +
                '}';
    }
} 