package cn.cuilan.bmp.utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回的结果对象，通过静态方法 success() 或 error() 获取对象。
 *
 * @author zhang.yan
 * @date 2019/7/4
 */
public class Results {

    /**
     * errorCode属性，默认值为失败：100001
     */
    private String errorCode = ErrorCode.ERROR;

    /**
     * message属性，默认值为失败：失败
     */
    private String message = ErrorCode.getMessageByErrorCode(ErrorCode.ERROR);

    /**
     * result结果集，默认为空。
     */
    private Object result = null;

    public String getErrorCode() {
        return errorCode;
    }

    private Results setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    private Results setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public Results setResult(Object result) {
        this.result = result;
        return this;
    }

    /**
     * 通用成功返回值
     */
    public static Results success() {
        return new Results().result(ErrorCode.SUCCESS);
    }

    /**
     * 通用失败返回值
     */
    public static Results error() {
        return new Results().result(ErrorCode.ERROR);
    }

    /**
     * 设置自定义状态码，并返回默认状态信息
     *
     * @param errorCode 状态码
     * @return 返回结果
     */
    public Results result(String errorCode) {
        return result(errorCode, ErrorCode.getMessageByErrorCode(errorCode));
    }

    /**
     * 可设置自定义的状态码和状态信息，返回值中结果默认为空
     *
     * @param errorCode 状态码
     * @param message   状态信息
     * @return 返回结果
     */
    public Results result(String errorCode, String message) {
        return result(errorCode, message, null);
    }

    /**
     * 可设置自定义的状态码、状态信息和结果集对象
     *
     * @param errorCode 状态码
     * @param message   状态信息
     * @param object    结果集对象
     * @return 返回结果
     */
    public Results result(String errorCode, String message, Object object) {
        return this.setErrorCode(errorCode)
                .setMessage(message)
                .setResult(object);
    }

    /**
     * 向集合中设置值
     *
     * @param object 结果集对象
     * @return 返回结果
     */
    public Results data(Object object) {
        return this.setResult(object);
    }

    /**
     * 向集合中添加map
     *
     * @param key   键
     * @param value 值
     * @return 返回结果
     */
    public Results data(Object key, Object value) {
        Map<Object, Object> data = new HashMap<>(2);
        data.put(key, value);
        return this.setResult(data);
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString();
    }

}
