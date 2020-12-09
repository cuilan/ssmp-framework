package cn.cuilan.ssmp.observer;

import cn.cuilan.ssmp.common.BaseObservableEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * 抽象实体观察者
 *
 * @param <T> BaseObservableEntity子类实体
 * @author zhang.yan
 * @date 2019-12-31
 */
@Slf4j
public abstract class AbstractDataObserver<T extends BaseObservableEntity<Long>> implements InitializingBean {

    // 实体创建观察者
    AbstractDataCreateObserver<T> dataCreateObserver;

    // 实体更新观察者
    AbstractDataUpdateObserver<T> dataUpdateObserver;

    // 实体删除观察者
    AbstractDataDeleteObserver<T> dataDeleteObserver;

    /**
     * 构造器
     *
     * @param observerMapper 实体Mapper
     */
    public AbstractDataObserver(BaseMapper<T> observerMapper) {
        // 初始化创建观察者
        dataCreateObserver = new AbstractDataCreateObserver<T>(observerMapper) {
            @Override
            protected void regCreateObserver(AbstractDataCreateObserver<T>.Register register) {
                AbstractDataObserver.this.regCreateObserver(register);
            }
        };
        // 初始化更新观察者
        dataUpdateObserver = new AbstractDataUpdateObserver<T>(observerMapper) {
            @Override
            protected void regUpdateObserver(AbstractDataUpdateObserver<T>.Register register) {
                AbstractDataObserver.this.regUpdateObserver(register);
            }
        };
        // 初始化删除观察者
        dataDeleteObserver = new AbstractDataDeleteObserver<T>(observerMapper) {
            @Override
            protected void regDeleteObserver(Register register) {
                AbstractDataObserver.this.regDeleteObserver(register);
            }
        };
    }

    @Override
    public final void afterPropertiesSet() {
        Type[] typeParams = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        if (typeParams.length != 1) {
            throw new RuntimeException(String.format("初始化观察者失败,观察者父类需要设置泛型[%s]", this.getClass().getSimpleName()));
        }
        Class<?> entityClass = (Class<?>) typeParams[0];
        dataCreateObserver.entityClass = entityClass;
        dataCreateObserver.afterPropertiesSet();
        dataUpdateObserver.entityClass = entityClass;
        dataUpdateObserver.afterPropertiesSet();
        dataDeleteObserver.entityClass = entityClass;
        dataDeleteObserver.afterPropertiesSet();
    }

    protected abstract void regCreateObserver(AbstractDataCreateObserver<T>.Register register);

    protected abstract void regUpdateObserver(AbstractDataUpdateObserver<T>.Register register);

    protected abstract void regDeleteObserver(AbstractDataDeleteObserver<T>.Register register);

    final void create(T t, Function<T, Object> consumer) {
        dataCreateObserver.create(t, consumer);
    }

    final void create(T t, Function<T, Object> consumer, ObserverContext<T> context) {
        dataCreateObserver.create(t, consumer, context);
    }

    final void update(T updated, Function<T, Object> consumer) {
        dataUpdateObserver.update(updated, consumer);
    }

    final void update(T updated, Function<T, Object> consumer, ObserverContext<T> context) {
        dataUpdateObserver.update(updated, consumer, context);
    }

    final void delete(T t, Function<T, Object> consumer) {
        dataDeleteObserver.delete(t, consumer);
    }

    final void delete(T t, Function<T, Object> consumer, ObserverContext<T> context) {
        dataDeleteObserver.delete(t, consumer, context);
    }
}
