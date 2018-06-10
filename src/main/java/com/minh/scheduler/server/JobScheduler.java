package com.minh.scheduler.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * The scheduler would give each client a computation slice of 4s each.  Each client needs to keep
 * pinging the server to keep its job slot or otherwise, after a configurable time-out (30s), server would drop
 * the client by decommission its corresponding job.
 */
public class JobScheduler {
    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    ExecutorService schedulerRunner = Executors.newFixedThreadPool(1);
    boolean hasStarted = false;
    Queue<Job> jobQueue = new ConcurrentLinkedQueue();
    Map<String, Job> jobStates = new ConcurrentHashMap<>();

    Configuration configuration;

    public JobScheduler(Configuration configuration) {
        this.configuration = configuration;
    }

    public void start() {
        if (!hasStarted) {
            logger.info("Starting scheduler thread!");
            schedulerRunner.submit(() -> {
                while (true) {
                    if (!jobQueue.isEmpty()) {
                        Job jc = jobQueue.poll();
                        runJob(jc);
                    }
                }
            });
        }
    }

    public void stop() {  //TODO: need to shut down the job cleanly by waiting for the running job to finish
        logger.info("Shutting down scheduler");
        schedulerRunner.shutdown();
    }

    public void scheduleJob(Job job) {
        if (!jobStates.containsKey(job.clientId)) {
            logger.info("Scheduling job for a new client: " + job.clientId);
            job.resetPingTime();
            jobQueue.add(job);
            jobStates.put(job.clientId, job);
        } else { //update ping time and based on this, we will remove a job if there is no heartbeat
            logger.info("Recording heartbeat for client: " + job.clientId);
            Job savedJob = jobStates.get(job.clientId);
            savedJob.resetPingTime();
        }
    }

    //Used in junit test only
    public Map<String, Job> jobStates() {
        Map<String, Job> jobs = new HashMap();
        jobs.putAll(jobStates);
        return jobs;
    }

    private void runJob(Job job) {
        if (canRunjob(job)) {
            new JobExecutor().execute(job);
            jobQueue.add(job); //reschedule again
        } else {
            deleteJob(job);
        }
    }

    private boolean canRunjob(Job job) {
        return (job.clientLastPing.get() + configuration.getHeartBeatTimeoutInMilliSecs()) > System.currentTimeMillis();
    }
    private void deleteJob(Job job) {
        logger.info("Unscheduling client job: " + job.clientId);
        jobQueue.remove(job);
        jobStates.remove(job.clientId);
    }
}
