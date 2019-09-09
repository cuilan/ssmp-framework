package cn.cuilan.framework.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

public class ExecutorUtil {

    private static ExecutorService es = Executors.newFixedThreadPool(30, new CustomizableThreadFactory("common_thread"));

    private static Logger logger = LoggerFactory.getLogger(ExecutorUtil.class);

    public static void execute(Runnable command) {
        if (es != null) {
            logger.info("线程执行");
            es.execute(command);
        } else {
            logger.error("线程初始化异常");
        }
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(int corePoolSize,
                                                           int maximumPoolSize,
                                                           long keepAliveTime,
                                                           TimeUnit unit,
                                                           BlockingQueue<Runnable> workQueue,
                                                           String name) {
        if (unit == null) {
            unit = TimeUnit.SECONDS;
        }
        if (StringUtils.isEmpty(name)) {
            return new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    unit,
                    workQueue, Executors.defaultThreadFactory());
        }
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue, new CustomizableThreadFactory(name));
    }

    public static ExecutorService newFixedThreadPool(int nThreads, String name) {
        if (StringUtils.isEmpty(name)) {
            return Executors.newFixedThreadPool(nThreads, Executors.defaultThreadFactory());
        }
        return Executors.newFixedThreadPool(nThreads, new CustomizableThreadFactory(name));
    }
}
