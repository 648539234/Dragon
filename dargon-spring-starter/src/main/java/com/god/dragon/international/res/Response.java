package com.god.dragon.international.res;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @implNote start with 2024/10/8 16:42
 */
@Slf4j
@Getter
@Setter
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    private String code;
    private String msg;
    private T data;
    public Response() {}
    public Response(boolean success) {
        this.success = success;
        if (success) {
            code = "000000";
        }
    }
    public Response(boolean success, T data) {
        this(success);
        this.data = data;
    }

    public static <T> Response<T> success() {
        return new Response<T>(true);
    }

    public static <T> Response<T> success(T data) {
        return new Response<T>(true, data);
    }

    public static <T> Response<T> failed() {
        return new Response<T>(false);
    }

    public static <T> Response<T> failed(String msg) {
        return new Response<T>(false).msg(msg);
    }

    public static <T> Response<T> failed(String code,String msg) {
        return new Response<T>(false).code(code).msg(msg);
    }

    public static <T> Response<T> failed(String code,String msg,String traceId) {
        return new Response<T>(false).code(code).msg(msg).data(traceId);
    }

    public static boolean isSuccess(Response<?> response) {
        return response!=null && response.success;
    }

    public Response<T> data(String msg) {
        this.data = data;
        return this;
    }

    public Response<T> code(String code) {
        this.code = code;
        return this;
    }

    public Response<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Response<T> msg(String msgPattern, Object... args) {
        List<String> paramNew = Arrays.stream(args).map(a-> a==null? "":a.toString()).collect(Collectors.toList());
        msgPattern = msgPattern.replaceAll("\\{\\}","%s");
        msg = String.format(msgPattern, paramNew);
        return this;
    }

    public Response<T> log() {
        log.info(msg);
        return this;
    }


}
