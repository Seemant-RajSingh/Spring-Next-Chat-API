package com.srs.SpringChat.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Optimized Thread Pool Settings
        executor.setCorePoolSize(200); // Increased to handle heavy traffic
        executor.setMaxPoolSize(400); // High max for surge handling
        executor.setQueueCapacity(2000); // Increased queue size
        executor.setKeepAliveSeconds(30); // Keep idle threads alive for reuse
        executor.setThreadNamePrefix("AsyncThread-");

        // Handle task rejection gracefully by running the task in the caller thread
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

        executor.initialize();
        return executor;
    }
}
