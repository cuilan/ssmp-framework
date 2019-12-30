package cn.cuilan.ssmp.exception.handler;

import cn.cuilan.ssmp.exception.BaseException;
import cn.cuilan.ssmp.utils.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一异常处理
 */
@Component
@Slf4j
public class BaseExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         Object object, Exception exception) {

        if (exception instanceof BindException) {
            BindException ex = (BindException) exception;
            List<ObjectError> allErrors = ex.getAllErrors();
            ObjectError error = allErrors.get(0);
            String defaultMessage = error.getDefaultMessage();

            log.info("参数校验不通过 [uri={},message={}]", httpServletRequest.getRequestURI(), Arrays.toString(error.getCodes()));
            return transforMv(Result.FAIL.getValue(), defaultMessage + ":" + error.getCodes()[1]);
        }
        log.error("系统异常", exception);

        if (exception instanceof BaseException) {
            return transforMv(((BaseException) exception).getCode(), exception.getMessage());
        }
        if (exception instanceof IllegalArgumentException) {
            return transforMv(Result.FAIL.getValue(), exception.getMessage());
        }
        if (exception instanceof SQLException) {
            return transforMv(Result.FAIL.getValue(), "数据库操作失败");
        }
        return transforMv(Result.FAIL.getValue(), exception.getMessage());
    }

    /**
     * 转换为ModelAndView
     *
     * @param code 异常状态码
     * @param msg  异常信息
     * @return ModelAndView
     */
    private ModelAndView transforMv(Integer code, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", msg);
        return new ModelAndView(new MappingJackson2JsonView(), map);
    }
}
