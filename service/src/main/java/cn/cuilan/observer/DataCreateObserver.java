package cn.cuilan.observer;

import cn.cuilan.common.BaseObservableEntity;
import cn.cuilan.observer.handler.CreateHandler;
import cn.cuilan.observer.handler.CreateHandlerWithContext;
import cn.cuilan.utils.ListenJvmShutdown;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public abstract class DataCreateObserver<T extends BaseObservableEntity> implements InitializingBean {

    protected static Map<Class<BaseObservableEntity>, DataCreateObserver> createObserverMap = new HashMap<>();

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNamePrefix("observer-pool-%d").build();

    protected static ExecutorService executorService = new ThreadPoolExecutor(0, 50,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), namedThreadFactory);

    BaseMapper<T> baseMapper;

    Class entityClass;

    private List<CreateHandlerWithContext<T>> beforeHandlerList = new ArrayList<>();
    private List<CreateHandlerWithContext<T>> afterHandlerList = new ArrayList<>();
    private List<CreateHandlerWithContext<T>> afterCommitHandlerList = new ArrayList<>();

    // 构造器
    public DataCreateObserver(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    <R> R create(T t, Function<T, R> createFunction) {
        ObserverContext context = new ObserverContext();
        return create(t, createFunction, context);
    }

    <R> R create(T t, Function<T, R> createFunction, ObserverContext context) {
        for (CreateHandlerWithContext<T> processor : beforeHandlerList) {
            try {
                processor.handler(t, context);
            } catch (Exception e) {
                log.error("create observer before handle, entity: {}", JSON.toJSONString(t), e);
                throw e;
            }
        }
        R result = createFunction.apply(t);
        for (CreateHandlerWithContext<T> processor : afterHandlerList) {
            try {
                processor.handler(t, context);
            } catch (Exception e) {
                log.error("create observer after handle, entity: {}", JSON.toJSONString(t), e);
                throw e;
            }
        }

        Supplier afterCommit = () -> {
            executorService.execute(() -> {
                for (CreateHandlerWithContext<T> processor : afterCommitHandlerList) {
                    try {
                        processor.handler(t, context);
                    } catch (Exception e) {
                        log.error("create observer after commit handle, entity: {}", JSON.toJSONString(t), e);
                    }
                }
            });
            return null;
        };

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            //如果事务开启
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    afterCommit.get();
                }
            });
        } else {
            //如果事务没有开启
            afterCommit.get();
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() {
        regCreateObserver(new Register());
        if (entityClass == null) {
            Type[] typeParams = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
            if (typeParams.length != 1) {
                throw new RuntimeException(String.format("初始化观察者失败，观察者父类必须设置泛型-[%s]", this.getClass().getSimpleName()));
            }
            entityClass = (Class) typeParams[0];
        }
        DataCreateObserver dataCreateObserver = createObserverMap.get(entityClass);

        if (dataCreateObserver != null) {
            throw new RuntimeException(String.format("%s的观察者存在多个[%s,%s]",
                    entityClass.getSimpleName(),
                    dataCreateObserver.getClass().getCanonicalName(),
                    this.getClass().getCanonicalName()
            ));
        }
        createObserverMap.put(entityClass, this);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> ListenJvmShutdown.shutdownThreadPool(executorService, "DataCreateObserver-Shutdown")));

    }

    protected abstract void regCreateObserver(Register register);

    public class Register {
        public void beforeCreate(String des, CreateHandlerWithContext<T> processor) {
            beforeHandlerList.add(processor);
        }

        public void beforeCreate(String des, CreateHandler<T> processor) {
            beforeHandlerList.add((obj, context) -> processor.handler(obj));
        }

        public void afterCreate(String des, CreateHandlerWithContext<T> processor) {
            afterHandlerList.add(processor);
        }

        public void afterCreate(String des, CreateHandler<T> processor) {
            afterHandlerList.add((obj, context) -> processor.handler(obj));
        }

        public void afterCreateCommit(String des, CreateHandlerWithContext<T> processor) {
            afterCommitHandlerList.add(processor);
        }

        public void afterCreateCommit(String des, CreateHandler<T> processor) {
            afterCommitHandlerList.add((obj, context) -> processor.handler(obj));
        }
    }

}
