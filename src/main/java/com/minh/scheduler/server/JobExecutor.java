package com.minh.scheduler.server;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.minh.scheduler.utils.Utils;

public class JobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);

    public void execute(Job job) {
        logger.info("Client: " + job.clientId + ", Counter value: " + job.context.lastId);
        Utils.sleep(Duration.ofMillis(1000));

        logger.info("Client: " + job.clientId + ", Counter value: " + job.context.lastId);
        Utils.sleep(Duration.ofMillis(1000));

        logger.info("Client: " + job.clientId + ", Counter value: " + job.context.lastId);
        Utils.sleep(Duration.ofMillis(1000));

        logger.info("Client: " + job.clientId + ", Counter value: " + job.context.lastId);
        Utils.sleep(Duration.ofMillis(1000));

        updateJob(job);
    }

    private void updateJob(Job job) {
        job.context.lastId = job.context.lastId + 1;
        job.context.lastRun = System.currentTimeMillis();
        job.context.numRuns++;
    }
}
