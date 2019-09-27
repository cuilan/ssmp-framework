package cn.cuilan.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ListenJvmShutdown {

    static ConcurrentHashMap<ExecutorService, String> map = new ConcurrentHashMap<>();

    /**
     * tomcat | jetty | Undertow
     * http://www.spring4all.com/article/1022 参考，暂未实现，采用 jvm
     *
     * @param executorService
     * @param alias
     */
    public static void put(ExecutorService executorService, String alias) {
        map.put(executorService, alias);
    }

    /**
     * 优雅关闭线程池
     *
     * @param threadPool
     * @param alias      https://my.oschina.net/u/3768341/blog/1842994
     */
    public static void shutdownThreadPool(ExecutorService threadPool, String alias) {
        log.info("Start to shutdown the thead pool: {}", alias);
        threadPool.shutdown(); // 使新任务无法提交.
        try {
            // 等待未完成任务结束
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                threadPool.shutdownNow(); // 取消当前执行的任务
                log.warn("Interrupt the worker, which may cause some task inconsistent. Please check the biz logs.");

                // 等待任务取消的响应
                if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.error("Thread pool can't be shutdown even with interrupting worker threads, which may cause some task inconsistent. Please check the biz logs.");
                }
            }
        } catch (InterruptedException ie) {
            // 重新取消当前线程进行中断
            threadPool.shutdownNow();
            log.error("The current server thread is interrupted when it is trying to stop the worker threads. This may leave an inconcistent state. Please check the biz logs.");
            // 保留中断状态
            Thread.currentThread().interrupt();
        }
        log.info("Finally shutdown the thead pool: {}", alias);
    }

}
