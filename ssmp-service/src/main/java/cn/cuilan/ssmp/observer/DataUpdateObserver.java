package cn.cuilan.ssmp.observer;

import cn.cuilan.ssmp.common.BaseObservableEntity;
import cn.cuilan.ssmp.observer.handler.UpdateHandler;
import cn.cuilan.ssmp.observer.handler.UpdateHandlerWithContext;
import cn.cuilan.ssmp.utils.ListenJvmShutdown;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author liudajiang
 */
@Slf4j
public abstract class DataUpdateObserver<T extends BaseObservableEntity> implements InitializingBean {

    static Map<Class<BaseObservableEntity>, DataUpdateObserver> updateObserverMap = new HashMap<>();

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNamePrefix("observer-pool-%d").build();

    protected static ExecutorService executorService = new ThreadPoolExecutor(0, 50,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), namedThreadFactory);

    Class entityClass;

    BaseMapper baseMapper;

    List<UpdateHandlerWithContext<T>> beforeHandlerList = new ArrayList<>();
    List<UpdateHandlerWithContext<T>> afterHandlerList = new ArrayList<>();
    List<UpdateHandlerWithContext<T>> afterCommitHandlerList = new ArrayList<>();

    // 构造器
    public DataUpdateObserver(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    <R> R update(T updated, Function<T, R> updateFunction) {
        return update(updated, updateFunction, new ObserverContext());
    }

    <R> R update(T updated, Function<T, R> updateFunction, ObserverContext context) {
        Serializable updatedId = updated.getId();
        if (updatedId == null) {
            throw new RuntimeException("update 失败，更新对象没有id");
        }
        if (!(updatedId instanceof Long)) {
            throw new RuntimeException("update 失败，更新对象没有id");
        }
        Long id = (Long) updatedId;
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        long random = System.nanoTime();
        queryWrapper.eq("id", id).eq("" + random, random);
        T old = (T) baseMapper.selectOne(queryWrapper);
        for (UpdateHandlerWithContext handler : beforeHandlerList) {
            try {
                handler.handler(old, updated, context);
            } catch (Exception e) {
                log.error("update observer before handle, entity:{}", JSON.toJSONString(old), e);
                throw e;
            }
        }
        R result = updateFunction.apply(updated);
        for (UpdateHandlerWithContext handler : afterHandlerList) {
            try {
                handler.handler(old, updated, context);
            } catch (Exception e) {
                log.error("update observer after handle, entity: {}", JSON.toJSONString(old), e);
                throw e;
            }
        }
        Supplier afterCommit = () -> {
            executorService.execute(() -> {
                for (UpdateHandlerWithContext<T> processor : afterCommitHandlerList) {
                    try {
                        processor.handler(old, updated, context);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("update after comment handle, entity: {}", JSON.toJSONString(old), e);
                    }
                }
            });
            return null;
        };
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    afterCommit.get();
                }
            });
        } else {
            afterCommit.get();
        }
        return result;
    }

    @Override
    public final void afterPropertiesSet() {
        regUpdateObserver(new Register());
        if (entityClass == null) {
            Type[] typeParams = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
            if (typeParams.length != 1) {
                throw new RuntimeException(String.format("初始化观察者失败，观察者父类必须设置泛型-[%s]", this.getClass().getSimpleName()));
            }
            entityClass = (Class) typeParams[0];
        }
        DataUpdateObserver dataCreateObserver = updateObserverMap.get(entityClass);

        if (dataCreateObserver != null) {
            throw new RuntimeException(String.format("%s的观察者存在多个[%s,%s]",
                    entityClass.getSimpleName(),
                    dataCreateObserver.getClass().getCanonicalName(),
                    this.getClass().getCanonicalName()
            ));
        }
        updateObserverMap.put(entityClass, this);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> ListenJvmShutdown.shutdownThreadPool(executorService, "DataUpdateObserver-Shutdown")));
    }

    protected abstract void regUpdateObserver(Register register);

    public class Register {
        public void beforeUpdate(String des, UpdateHandler<T> handler) {
            beforeHandlerList.add((oldObj, newObj, context) -> handler.handler(oldObj, newObj));
        }

        public void beforeUpdate(String des, UpdateHandlerWithContext<T> handler) {
            beforeHandlerList.add(handler);
        }

        /**
         * 与数据库操作共享一个事务
         *
         * @param des
         * @param handler
         */
        public void afterUpdate(String des, UpdateHandler<T> handler) {
            afterHandlerList.add((oldObj, newObj, context) -> handler.handler(oldObj, newObj));
        }

        /**
         * 与数据库操作共享一个事务
         *
         * @param des
         * @param handler
         */
        public void afterUpdate(String des, UpdateHandlerWithContext<T> handler) {
            afterHandlerList.add(handler);
        }

        /**
         * 同步执行
         * 与数据库操作事务分离
         */
        public void afterUpdateCommit(String des, UpdateHandler<T> handler) {
            afterCommitHandlerList.add((oldObj, newObj, context) -> handler.handler(oldObj, newObj));
        }

        /**
         * 异步执行
         * 与数据库操作事务分离
         */
        public void afterUpdateCommit(String des, UpdateHandlerWithContext<T> handler) {
            afterCommitHandlerList.add(handler);
        }
    }
}
