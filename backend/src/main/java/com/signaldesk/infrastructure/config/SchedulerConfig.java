package com.signaldesk.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Scheduling is enabled via @EnableScheduling.
    // Actual scheduled methods live in CrawlScheduler.
}
