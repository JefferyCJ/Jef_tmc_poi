package com.tumi.data.poi.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/24 7:42 PM
 * @describe:
 */
@Configuration
@EnableAsync
public class TaskExecutorConfig implements AsyncConfigurer {

    @Bean("taskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //线程池大小
        taskExecutor.setCorePoolSize(5);
        //线程池最大线程数
        taskExecutor.setMaxPoolSize(10);
        //最大等待任务数
        taskExecutor.setQueueCapacity(25);
        taskExecutor.setKeepAliveSeconds(60);
        // 设置拒绝策略
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.initialize();

        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
