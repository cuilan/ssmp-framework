package cn.cuilan.ssmp.utils.result;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * response输出工具
 *
 * @author zhang.yan
 * @date 2019-12-30
 */
@Slf4j
public class ResultUtil {

    /**
     * 私有化构造器
     */
    private ResultUtil() {
    }

    /**
     * 使用response输出JSON
     */
    public static void responseJson(HttpServletResponse response, Object object) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            out.println(JSON.toJSONString(object));
        } catch (Exception e) {
            log.error("JSON输出异常: " + e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * 返回成功示例
     */
    public static Map<String, Object> resultSuccess(Map<String, Object> resultMap) {
        resultMap.put("message", "操作成功");
        resultMap.put("code", 200);
        return resultMap;
    }

    /**
     * 返回失败示例
     */
    public static Map<String, Object> resultError(Map<String, Object> resultMap) {
        resultMap.put("message", "操作失败");
        resultMap.put("code", 400);
        return resultMap;
    }

    /**
     * 通用示例
     */
    public static Map<String, Object> resultCode(Integer code, String msg) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("message", msg);
        resultMap.put("code", code);
        return resultMap;
    }
}
