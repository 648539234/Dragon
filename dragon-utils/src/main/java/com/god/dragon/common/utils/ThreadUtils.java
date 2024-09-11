package com.god.dragon.common.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池支持MDC-traceId向下传递,支持submit报错日志捕获
 * @author wuyuxiang
 * @version 1.0.0
 * @implNote start with 2024/9/11 11:06
 */
public class ThreadUtils {
    private static final ExecutorService THREAD_POOL = new CustomerThreadPoolExecutor(32,
            64,
            5,
            TimeUnit.MINUTES,
            new SynchronousQueue<>(), //队列不存,有消息立刻交由线程池去处理
            new ThreadFactoryBuilder().setNameFormat("async-thread-pool-%d").setDaemon(false).setPriority(Thread.NORM_PRIORITY).build(),
            new ThreadPoolExecutor.CallerRunsPolicy() //由原线程池执行,建议使用submit提交否则执行任务报错,主线程后续代码也不会执行
    );

    public static ExecutorService getThreadPool() { return THREAD_POOL;}

    public static class CustomerThreadPoolExecutor extends ThreadPoolExecutor {
        public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        }

        public CustomerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        /**
         * 所有的线程池执行的任务的方法最后都会走该方法,重写Runnable支持MDC的传递,支持traceId子线程打印
         * @param command the task to execute
         */
        @Override
        public void execute(Runnable command) {
            Runnable dtpRunnable = new MdcRunnable(command);
            super.execute(command);
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
            return new ConsoleExceptionFutureTask<T>(callable);
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
            return new ConsoleExceptionFutureTask<T>(runnable, value);
        }
    }

    //支持父线程MDC内容传递给子线程
    public static class MdcRunnable implements Runnable {
        private final Runnable runnable;
        private final Map<String, String> parentMdc;

        public MdcRunnable(Runnable runnable) {
            this.runnable = runnable;
            this.parentMdc = MDC.getCopyOfContextMap();
        }

        @Override
        public void run() {
            if(parentMdc != null || parentMdc.isEmpty()) {
                runnable.run();
                return;
            }

            for (Map.Entry<String, String> entry : parentMdc.entrySet()) {
                MDC.put(entry.getKey(), entry.getValue());
            }

            try{
                runnable.run();
            }finally {
                MDC.clear();
            }
        }
    }

    /**
     * 当使用线程池的submit方法时,如果线程池内运行的任务中抛出异常时,没有做捕获或者异常打印,线程池是不会抛出异常或者打印的,
     * 除非主动去调用submit方法返回的响应结果才会捕获异常,所以此处加了日志打印,打印submit方法时任务未捕获的异常
     * @param <V>
     */
    @Slf4j
    public static class ConsoleExceptionFutureTask<V> extends FutureTask<V> {

        public ConsoleExceptionFutureTask(Callable<V> callable) {
            super(callable);
        }

        public ConsoleExceptionFutureTask(Runnable runnable, V result) {
            super(runnable, result);
        }

        @Override
        protected void setException(Throwable t) {
            log.error("线程池执行任务异常", t);
            super.setException(t);
        }
    }

}
