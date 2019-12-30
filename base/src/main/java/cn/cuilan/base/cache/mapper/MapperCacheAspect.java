package cn.cuilan.base.cache.mapper;

import cn.cuilan.base.entity.SuperIdEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.cuilan.base.cache.Caches;
import cn.cuilan.base.cache.scenario.ValueCache;
import cn.cuilan.base.cache.utils.CollectionUtils;
import cn.cuilan.base.entity.IdEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Aspect
@Slf4j
public class MapperCacheAspect implements InitializingBean {
    private static final String ENTITY_PACKAGE = "cn.cuilan.ssmp.entity";
    public static Map<Class<IdEntity>, ValueCache<Long, IdEntity>> entityCacheMap = new HashMap<>();
    private Method baseMapperSelectBatchIds;

    public MapperCacheAspect() {
        try {
            baseMapperSelectBatchIds = BaseMapper.class.getDeclaredMethod("selectBatchIds", Collection.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除entity的缓存
     */
    public static void deleteEntityCache(Class<? extends IdEntity> clazz, Long id) {
        ValueCache<Long, IdEntity> entityCache = entityCacheMap.get(clazz);
        if (entityCache == null) {
            return;
        }
        entityCache.del(id);
    }

    @Around("execution(* cn.cuilan.service.*.mapper.*.evictCache(..))")
    public Object interceptEvictCache(ProceedingJoinPoint pjp) throws Throwable {
        Long id = (Long) pjp.getArgs()[0];
        Class entityClass = getMapperEntityClass(pjp);
        ValueCache entityCache = entityCacheMap.get(entityClass);
        if (entityCache == null) {
            log.warn("清除entity 缓存失败:找不到{}对应的缓存", entityClass.getSimpleName());
            return null;
        }
        entityCache.del(id);
        return null;
    }

    @Around("execution(* cn.cuilan.service.*.mapper.*.insert(..))")
    public Object interceptInsertCache(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        Object entity = pjp.getArgs()[0];
        if (entity instanceof IdEntity) {
            IdEntity idEntity = (IdEntity) entity;
            ValueCache entityCache = entityCacheMap.get(entity.getClass());
            if (entityCache != null && idEntity != null && idEntity.getId() != null) {
                entityCache.del(idEntity.getId());
            }
        }
        return result;
    }

    @Around("execution(* cn.cuilan.service.*.mapper.*.selectByIdCached(..))")
    public Object interceptSelectById(ProceedingJoinPoint pjp) throws Throwable {
        Long id = (Long) pjp.getArgs()[0];
        return getBatchFromCache(pjp, Arrays.asList(id)).get(0);
    }

    @Around("execution(* cn.cuilan.service.*.mapper.*.selectBatchIdsCached(..))")
    public Object interceptSelectBatchIds(ProceedingJoinPoint pjp) throws Throwable {
        List<Long> ids = new ArrayList<>((Collection<Long>) pjp.getArgs()[0]);
        return getBatchFromCache(pjp, ids);
    }

    @Around("execution(* cn.cuilan.service.*.mapper.*.selectBatchIdsMapCached(..))")
    public Object interceptSelectBatchIdsMap(ProceedingJoinPoint pjp) throws Throwable {
        List<Long> ids = new ArrayList<>((Collection<Long>) pjp.getArgs()[0]);
        return getBatchFromCache(pjp, ids).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SuperIdEntity::getId, n -> n, (k1, k2) -> k1));
    }

    @Around("execution(* cn.cuilan.service.*.mapper.*.updateById(..))")
    public Object interceptUpdateById(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed(pjp.getArgs());
        IdEntity entity = (IdEntity) pjp.getArgs()[0];
        log.info("update entity [class={},id={}]", entity.getClass().getSimpleName(), entity.getId());
        ValueCache entityCache = entityCacheMap.get(entity.getClass());
        if (entityCache != null) {
            log.info("clear entity cache[class={},id={}]", entity.getClass().getSimpleName(), entity.getId());
            entityCache.del(entity.getId());
        }
        return result;
    }

    private Class getMapperEntityClass(ProceedingJoinPoint pjp) {
        Class mapperClass = (Class) pjp.getTarget().getClass().getGenericInterfaces()[0];
        Class entityClass = (Class) ((ParameterizedType) mapperClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
        return entityClass;
    }

    private List<IdEntity> getBatchFromCache(ProceedingJoinPoint pjp, List<Long> ids) throws Throwable {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        Class entityClass = getMapperEntityClass(pjp);
        if (!IdEntity.class.isAssignableFrom(entityClass)) {
            return (List) pjp.proceed(pjp.getArgs());
        }
        ValueCache<Long, IdEntity> entityCache = entityCacheMap.get(entityClass);
        if (entityCache == null) {
            return (List) pjp.proceed(pjp.getArgs());
        }
        Map<Long, IdEntity> idEntityMap = entityCache.getAll(ids, ids1 -> {
            try {
                Map<Long, IdEntity> resultMap = new HashMap<>();
                List<IdEntity> list = (List<IdEntity>) baseMapperSelectBatchIds.invoke(pjp.getTarget(), ids1);
                for (IdEntity entity : list) {
                    resultMap.put(entity.getId(), entity);
                }
                log.debug("batch load entity from DB[entity={},ids={},result={}]", entityClass.getSimpleName(), ids1, resultMap);
                return resultMap;
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
        return ids.stream().map(idEntityMap::get).collect(Collectors.toList());
    }


    @Override
    public void afterPropertiesSet() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(IdEntity.class));
        Set<BeanDefinition> components = provider.findCandidateComponents(ENTITY_PACKAGE);
        Set<Class<IdEntity>> beanClassSet = new HashSet<>();

        for (BeanDefinition definition : components) {
            try {
                beanClassSet.add((Class<IdEntity>) Class.forName(definition.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (Class<IdEntity> idEntityClass : beanClassSet) {
            double version = 1.0D;
            EntityCacheVersion entityCacheVersion = idEntityClass.getAnnotation(EntityCacheVersion.class);
            if (entityCacheVersion != null) {
                version = entityCacheVersion.value();
            }
            ValueCache<Long, IdEntity> cache = Caches.forValue("Entity_" + idEntityClass.getSimpleName()
                    + version, Long.class, IdEntity.class)
                    .remoteExpire(20, TimeUnit.MINUTES)
                    .local(2000, 5, TimeUnit.SECONDS)
                    .build();
            entityCacheMap.put(idEntityClass, cache);
        }
    }
}
