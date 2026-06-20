package com.compliance.notification.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Single source of truth for @EnableScheduling and @EnableAsync. NOT duplicated
 * in NotificationScheduler.
 *
 * Thread pools: notificationSchedulerExecutor — main async dispatch pool
 * (email/push/sms combined) Used by @Async("notificationSchedulerExecutor") in
 * NotificationSenderServiceImpl.
 *
 * For 300M users / burst traffic: core=20, max=100, queue=2000. Tune via
 * app.scheduler.executor.* in application.yaml.
 */
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig {

  @Value("${app.scheduler.executor.core-pool-size:20}")
  private int corePoolSize;

  @Value("${app.scheduler.executor.max-pool-size:100}")
  private int maxPoolSize;

  @Value("${app.scheduler.executor.queue-capacity:2000}")
  private int queueCapacity;

  @Bean(name = "notificationSchedulerExecutor")
  public Executor notificationSchedulerExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix("notif-sender-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(60);
    executor.initialize();
    return executor;
  }
}
