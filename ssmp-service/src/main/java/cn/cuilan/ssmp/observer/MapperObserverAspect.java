package cn.cuilan.ssmp.observer;

import cn.cuilan.ssmp.common.BaseObservableEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 观察者AOP切面
 */
@Component
@Aspect
public class MapperObserverAspect {

    /**
     * 创建实体观察者切面处理
     */
    @Around("execution(* cn.cuilan.*.*Mapper.insert(..))")
    public Object interceptInsert(ProceedingJoinPoint pjp) throws Throwable {
        Object entity = pjp.getArgs()[0];
        if (!(entity instanceof BaseObservableEntity)) {
            return pjp.proceed(pjp.getArgs());
        }
        BaseObservableEntity observableEntity = (BaseObservableEntity) entity;
        DataCreateObserver dataCreateObserver = DataCreateObserver.createObserverMap.get(observableEntity.getClass());
        if (dataCreateObserver == null) {
            return pjp.proceed(pjp.getArgs());
        }
        ObserverContext observerContext;
        Function createFun;
        if (pjp.getArgs().length == 2 && pjp.getArgs()[1] instanceof ObserverContext) {
            observerContext = (ObserverContext) pjp.getArgs()[1];
            createFun = n -> {
                try {
                    Method method = BaseMapper.class.getDeclaredMethod("insert", Object.class);
                    return method.invoke(pjp.getTarget(), observableEntity);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        } else {
            observerContext = new ObserverContext();
            createFun = n -> {
                try {
                    return pjp.proceed(pjp.getArgs());
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            };
        }
        return dataCreateObserver.create(observableEntity, createFun, observerContext);
    }

    /**
     * 更新实体观察者切面处理
     */
    @Around("execution(* cn.cuilan.*.*Mapper.update*(..))")
    public Object interceptUpdateById(ProceedingJoinPoint pjp) throws Throwable {
        Object args = pjp.getArgs()[0];
        if (!(args instanceof BaseObservableEntity)) {
            return pjp.proceed(pjp.getArgs());
        }
        BaseObservableEntity observable = (BaseObservableEntity) args;
        DataUpdateObserver dataUpdateObserver = DataUpdateObserver.updateObserverMap.get(observable.getClass());
        if (dataUpdateObserver == null) {
            return pjp.proceed(pjp.getArgs());
        }
        return dataUpdateObserver.update(observable, n -> {
            try {
                return pjp.proceed(pjp.getArgs());
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
    }

    /**
     * 删除实体观察者切面处理
     */
    @Around("execution(* cn.cuilan.*.*Mapper.delete*(..))")
    public Object interceptDelete(ProceedingJoinPoint pjp) throws Throwable {
        Object args = pjp.getArgs()[0];
        if (!(args instanceof BaseObservableEntity)) {
            return pjp.proceed(pjp.getArgs());
        }
        BaseObservableEntity observable = (BaseObservableEntity) args;
        DataDeleteObserver dataDeleteObserver = DataDeleteObserver.deleteObserverMap.get(observable.getClass());
        if (dataDeleteObserver == null) {
            return pjp.proceed(pjp.getArgs());
        }
        return dataDeleteObserver.delete(observable, n -> {
            try {
                return pjp.proceed(pjp.getArgs());
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
    }

}
