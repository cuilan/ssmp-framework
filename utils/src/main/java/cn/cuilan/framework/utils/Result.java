package cn.cuilan.framework.utils;


import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
//import com.gozap.chouti.grpc.Response;

/**
 * service层返回给外部的model
 */
@Data
public class Result<T extends Object> {

    public static Code code_success = new Code(200);
    public static Code code_redirect = new Code(302);
    public static Code code_fail = new Code(400);
    public static Code code_noAuth = new Code(401);
    public static Code code_noPermission = new Code(402);
    public static Code code_no_access = new Code(403);
    public static Code LINK_EXIST = new Code(405);
    public static Code code_success_99999 = new Code(99999);
    public static Code CODE_40042 = new Code(40042);

    public static <T> Result<T> success(T data) {
        return new Result<>(code_success, data);
    }

    public static MapDataResult success() {
        return new MapDataResult(code_success);
    }

    public static Result successMsg(String msg) {
        return new Result<>(code_success, null, msg);
    }

    public static Result fail(String msg) {
        return new Result<>(code_fail, null, msg);
    }

    public static Result<?> fail(String msg, Integer errorType) {
        return new Result<>(code_fail, null, msg, errorType);
    }

    public static Result<?> noAuth() {
        return new Result<>(code_noAuth, null, "请登录后重试");
    }

    public static Result<?> destroy() {
        return new Result<>(code_noAuth, null, "账号已注销");
    }

    public static Result<?> noPermission() {
        return new Result<>(code_noPermission, null, "请授权或登录其他账号");
    }

    public static Result<?> linkExist(Object data, String msg) {
        return new Result<>(LINK_EXIST, data, msg);
    }

//    public static Result fromResponse(Response response) {
//        if (response.isFail()) {
//            return Result.fail(response.getMsg());
//        }
//        Result result=Result.success(response.getData());
//        result.setTotal(response.getTotal());
//        return result;
//    }

    /**
     * 结果数据，错误时通常此字段为空
     */
    private T data;
    private T result;//兼容ios要求的返回结果结构

    @Getter
    private Long total;
    /**
     * 结果信息，一般主要是用来说明错误原因
     */
    private String msg;
    private Integer code;

    private Integer errorType;

    /**
     * 一般返回结果为错误时没有数据 code为null则抛出NullPointerExcetion
     */
    public Result(Code code) {
        if (null == code) {
            throw new NullPointerException("code can not be null");
        }
        this.code = code.getValue();
    }


    /**
     * 一般返回结果为正确时用此带数据的构造函数
     */
    public Result(Code code, T data) {
        this(code);
        this.data = data;
    }

    public Result(Code code, T data, String msg) {
        this(code, data);
        this.msg = msg;
    }

    public Result(Code code, T data, String msg, Integer errorType) {
        this(code, data);
        this.msg = msg;
        this.errorType = errorType;
    }

    public T getData() {
        return this.data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public Result<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public Integer getErrorType() {
        return errorType;
    }

    public void setErrorType(Integer errorType) {
        this.errorType = errorType;
    }

    public void setCodeObj(Code code) {
        this.code = code.getValue();
    }

    public boolean hasError() {
        return this.code.equals(code_fail.getValue());
    }

    public boolean isSuccess() {
        return this.code.equals(code_success.getValue());
    }

    public static MapResult map() {
        return new MapResult();
    }


    public static class Code {
        private int value;

        public Code(int value) {
            super();
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static class MapDataResult extends Result<Map> {

        public MapDataResult(Code code) {
            super(code);
            super.data = new HashMap();
        }

        public MapDataResult data(String key, Object obj) {
            super.data.put(key, obj);
            return this;
        }

        public MapDataResult data(Map map) {
            super.data.putAll(map);
            return this;
        }
    }

    public Result total(long total) {
        this.total = total;
        return this;
    }
}
