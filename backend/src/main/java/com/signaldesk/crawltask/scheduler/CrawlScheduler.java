package com.signaldesk.crawltask.scheduler;

import com.signaldesk.crawltask.service.CrawlTaskService;
import com.signaldesk.source.domain.Source;
import com.signaldesk.source.repository.SourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CrawlScheduler {

    private static final Logger log = LoggerFactory.getLogger(CrawlScheduler.class);

    private final SourceRepository sourceRepository;
    private final CrawlTaskService crawlTaskService;

    public CrawlScheduler(SourceRepository sourceRepository,
                          CrawlTaskService crawlTaskService) {
        this.sourceRepository = sourceRepository;
        this.crawlTaskService = crawlTaskService;
    }

    /**
     * Scan every 60 seconds for sources that are due for fetch.
     */
    @Scheduled(fixedDelay = 60_000)
    public void scanDueSources() {
        List<Source> dueSources = sourceRepository.findSourcesDueForFetch(LocalDateTime.now());

        for (Source source : dueSources) {
            try {
                log.debug("Scheduled fetch triggered for source: {}", source.getId());
                crawlTaskService.triggerScheduledFetch(source.getId(), source.getUserId());
            } catch (Exception e) {
                log.error("Failed to trigger scheduled fetch for source: {}", source.getId(), e);
            }
        }
    }

    /**
     * Every 5 minutes, scan for failed tasks that can be retried.
     */
    @Scheduled(fixedDelay = 300_000)
    public void retryFailedTasks() {
        // This is handled by the user manually for now.
        // Could be extended to auto-retry failed SCHEDULED tasks.
        log.debug("Retry scan: manual retry only at this stage.");
    }
}
