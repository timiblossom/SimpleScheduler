package com.minh.scheduler.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import com.minh.scheduler.grpc.JobServiceGrpc;
import com.minh.scheduler.grpc.JobRequest;
import com.minh.scheduler.grpc.JobResponse;

public class JobServerRunner {
    private static final Logger logger = LoggerFactory.getLogger(JobServerRunner.class);

    public static  void main(String [] args) throws IOException, InterruptedException {
        Configuration envConfiguration = new Configuration();
        JobScheduler scheduler = new JobScheduler(envConfiguration);

        logger.info("Starting scheduler ...");
        scheduler.start();

        Server server = ServerBuilder.forPort(envConfiguration.getListenPort())
                .addService(new JobServiceImpl(scheduler)).build();

        logger.info("Starting server...");
        server.start();


        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                logger.error("*** shutting down gRPC server since JVM is shutting down");
                scheduler.stop();
                server.shutdown();
                logger.error("*** server shut down");
            }
        });

        logger.info("Server started!");
        server.awaitTermination();
    }

    public static class JobServiceImpl extends JobServiceGrpc.JobServiceImplBase {
        private Configuration configuration;
        private JobScheduler scheduler;

        public JobServiceImpl(JobScheduler scheduler) {
            this.scheduler = scheduler;
            this.configuration = scheduler.configuration;
        }

        @Override
        public void submit(JobRequest request, StreamObserver<JobResponse> responseObserver) {
            logger.info("Accepting client:  " + request.getClientId());
            Job job = new Job(request.getClientId());
            scheduler.scheduleJob(job);

            JobResponse response = JobResponse.newBuilder().setJobStatus("Accepted " + request.getClientId()).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}