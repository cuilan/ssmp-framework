package cn.cuilan.bmp.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 状态码常量类
 *
 * @author zhang.yan
 * @date 2019/7/4
 */
public class ErrorCode {

    /**
     * 成功
     */
    public final static String SUCCESS = "100000";

    /**
     * 失败
     */
    public final static String ERROR = "100001";

    /**
     * 参数为空
     */
    public final static String PARAM_IS_NULL = "100002";

    /**
     * 用户不存在
     */
    public final static String USER_UNEXSIT = "100003";

    /**
     * Token认证失败
     */
    public final static String AUTHORIZATION_FAIL = "100004";

    /**
     * Token失效
     */
    public final static String TOKEN_FAIL = "100005";

    /**
     * 服务器异常
     */
    public final static String SERVER_ERROR = "100006";

    /**
     * 存储错误码的map集合
     */
    private static Map<String, String> codeAndMessaeMap = new HashMap<>();

    static {
        codeAndMessaeMap.put(SUCCESS, "success");
        codeAndMessaeMap.put(ERROR, "error");
        codeAndMessaeMap.put(PARAM_IS_NULL, "参数为空");
        codeAndMessaeMap.put(USER_UNEXSIT, "用户名或密码错误");
        codeAndMessaeMap.put(AUTHORIZATION_FAIL, "Authorization Fail!");
        codeAndMessaeMap.put(TOKEN_FAIL, "Token失效!");
        codeAndMessaeMap.put(SERVER_ERROR, "服务器异常");
    }

    /**
     * 通过错误码获取错误信息
     *
     * @param code 错误码
     * @return 返回该错误码的提示信息
     */
    public static String getMessageByErrorCode(String code) {
        return codeAndMessaeMap.get(code);
    }
}
