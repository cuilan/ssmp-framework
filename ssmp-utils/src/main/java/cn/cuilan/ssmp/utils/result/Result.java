package cn.cuilan.ssmp.utils.result;

import com.github.pagehelper.Page;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装返回结果
 *
 * @param <T>
 */
public class Result<T extends Object> {

    // 成功
    private final static Code SUCCESS = new Code(200);

    // 失败
    public final static Code FAIL = new Code(400);

    // 未认证
    private final static Code NO_AUTH = new Code(401);

    // 无权限
    private final static Code NO_PERMISSION = new Code(402);

    // 用户不存在
    private final static Code USER_NOT_FOUND = new Code(403);

    // 密码不正确
    private final static Code WRONG_PASSWORD = new Code(404);

    // 错误码
    private Integer code;

    // 错误信息、说明描述等
    private String message;

    // 封装返回数据
    private T data;

    public Result() {
    }

    /**
     * 仅返回成功状态，无返回数据时使用
     *
     * @param code 错误码
     */
    public Result(Code code) {
        if (code == null) {
            throw new NullPointerException("状态码为空");
        }
        this.code = code.getValue();
    }

    /**
     * 仅返回成功状态及数据结果
     *
     * @param code 错误码
     * @param data 返回数据
     */
    public Result(Code code, T data) {
        this(code);
        if (data instanceof Page) {
            this.data = (T) new PageInfo((Page) data);
            return;
        }
        this.data = data;
    }

    /**
     * 返回错误码，错误说明信息，数据结果
     *
     * @param code    错误码
     * @param message 错误信息，描述信息
     * @param data    返回数据
     */
    public Result(Code code, String message, T data) {
        this(code, data);
        this.message = message;
    }

    public static MapResult map() {
        MapResult result = new MapResult();
        Map<String, Object> map = new HashMap<>();
        result.setData(map);
        result.setCode(SUCCESS.value);
        return result;
    }

    // 普通成功仅返回状态
    public static Result<?> success() {
        return new Result<>(SUCCESS, null);
    }

    // 返回成功状态及描述信息
    public static Result<?> success(String msg) {
        return new Result<>(SUCCESS, msg, null);
    }

    // 仅返回成功数据
    public static Result<?> success(Object data) {
        return new Result<>(SUCCESS, data);
    }

    // =====================================================================

    // 成功
    public static Result<?> success(String msg, Object data) {
        return new Result<>(SUCCESS, msg, data);
    }

    // 失败
    public static Result<?> fail(String msg) {
        return new Result<>(FAIL, msg, null);
    }

    // 未认证
    public static Result<?> noAuth() {
        return new Result<>(NO_AUTH, "请登录后重试", null);
    }

    // 无权限
    public static Result<?> noPermission() {
        return new Result<>(NO_PERMISSION, "请授权或登录其他账号", null);
    }

    // 用户不存在
    public static Result<?> userNotFound() {
        return new Result<>(USER_NOT_FOUND, "用户不存在", null);
    }

    // 密码不正确
    public static Result<?> wrongPassword() {
        return new Result<>(WRONG_PASSWORD, "用户不存在", null);
    }

    // =====================================================================

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static class MapResult extends Result<Map<String, Object>> {
        public MapResult data(String key, Object value) {
            this.getData().put(key, value);
            return this;
        }
    }

    /**
     * 错误码
     */
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
}
