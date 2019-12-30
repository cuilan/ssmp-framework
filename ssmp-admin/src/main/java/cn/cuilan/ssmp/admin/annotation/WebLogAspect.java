package cn.cuilan.ssmp.admin.annotation;

import com.google.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
@Profile("dev")
public class WebLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    /**
     * 换行符
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * 定义@WebLog注解的切点
     */
    @Pointcut("@annotation(cn.cuilan.ssmp.admin.annotation.WebLog)")
    public void webLog() {
    }

    /**
     * 前置通知
     *
     * @param joinPoint 切点
     * @throws ClassNotFoundException ClassNotFoundException
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws ClassNotFoundException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 获取@WebLog注解的描述信息
        String methodDescription = getAspectLogDescription(joinPoint);

        // 打印请求相关参数
        logger.info("================================= Start =================================");
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            // 打印请求url
            logger.info("URL:\t\t\t\t{}", request.getRequestURI());
            // 打印描述信息
            logger.info("Description:\t\t{}", methodDescription);
            // 打印Http method
            logger.info("HTTP Method:\t\t{}", request.getMethod());
            // 但因调用controller的全路径以及执行方法
            logger.info("Class Method:\t{}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            // 打印请求的ip
            logger.info("IP:\t\t\t\t{}", request.getRemoteAddr());
            // 打印请求入参
            logger.info("Request Args:\t{}", new Gson().toJson(joinPoint.getArgs()));
        }
    }

    /**
     * 后置通知
     */
    @After("webLog()")
    public void doAfter() {
        // 接口请求结束
        logger.info("=========================================================================" + LINE_SEPARATOR);
    }

    /**
     * 环绕通知
     *
     * @param proceedingJoinPoint 切点
     * @return response result
     * @throws Throwable 异常
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        // 打印出参
        logger.info("Response Args:\t{}", new Gson().toJson(result));
        // 打印执行耗时
        logger.info("Time:\t\t\t{}ms", System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 获取切面注解的描述
     *
     * @param joinPoint 切点
     * @return 描述信息
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public String getAspectLogDescription(JoinPoint joinPoint) throws ClassNotFoundException {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Class<?> targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        StringBuilder description = new StringBuilder("");
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == args.length) {
                    description.append(method.getAnnotation(WebLog.class).value());
                    break;
                }
            }
        }
        return description.toString();
    }

}
