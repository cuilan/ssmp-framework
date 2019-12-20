package cn.cuilan.ssmp.utils.interceptor;

import cn.cuilan.ssmp.utils.annotation.BannedForbidden;
import cn.cuilan.ssmp.utils.enumUtils.BannedTypeEnum;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BannedForbiddeInterceptor implements HandlerInterceptor {

    Map<BannedTypeEnum, TypeHandler> typeMethodMap = new HashMap<>();

    public BannedForbiddeInterceptor() {
        typeMethodMap.put(BannedTypeEnum.USER, this::isUserBanned);
        typeMethodMap.put(BannedTypeEnum.IP, this::isIpBanned);
        typeMethodMap.put(BannedTypeEnum.PHONE, this::isPhoneBanned);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if ("/error".equals(request.getRequestURI())) {
            return true;
        }
        if ("/health".equals(request.getRequestURI()) || "/actuator/health".equals(request.getRequestURI())) {
            return true;
        }
        if (request.getRequestURI().startsWith("/swagger")) {
            return true;
        }
        BannedForbidden methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(BannedForbidden.class);
        if (null == methodAnnotation) {
            return true;
        }
        BannedTypeEnum[] bannedTypeEnums = methodAnnotation.value();
        Set<BannedTypeEnum> bannedTypeEnumSet = Sets.newHashSet(bannedTypeEnums);
        String bannedFromType = isBannedFromType(bannedTypeEnumSet, request);
        if (StringUtils.isNotBlank(bannedFromType)) {
            //只抛异常，然后在全局异常处理这里进行处理
            handleError(bannedFromType);
            return false;
        }
        return true;
    }

    String isBannedFromType(Set<BannedTypeEnum> typeEnumSet, HttpServletRequest request) {
        if (typeEnumSet.contains(BannedTypeEnum.ALL)) {
            typeEnumSet = Sets.newHashSet(BannedTypeEnum.USER, BannedTypeEnum.IP, BannedTypeEnum.PHONE);
        }
        for (BannedTypeEnum bannedTypeEnum : typeEnumSet) {
            TypeHandler typeHandler = typeMethodMap.get(bannedTypeEnum);
            if (typeHandler == null) {
                continue;
            }
            String handle = typeHandler.handle(request);
            if (StringUtils.isNotBlank(handle)) {
                return handle;
            }
        }
        return null;
    }
    public abstract void handleError(String errorMsg);

    public abstract String isUserBanned(HttpServletRequest request);

    public abstract String isIpBanned(HttpServletRequest request);

    public abstract String isPhoneBanned(HttpServletRequest request);

    @FunctionalInterface
    interface TypeHandler {
        String handle(HttpServletRequest request);
    }
}
