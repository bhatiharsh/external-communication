package com.external_api.service;


import com.external_api.dto.QueueResponse;
import com.external_api.externalrebootservice.ExternalReboot;
import com.external_api.precheck.PreCheck;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@org.springframework.stereotype.Service
@Slf4j
public class Service {

    private final RebootQueueService rebootQueueService;
    private final ExternalReboot externalReboot;

    private final PreCheck preCheck;

    private volatile boolean running = true;


    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Service(RebootQueueService rebootQueueService, ExternalReboot externalReboot, PreCheck preCheck) {
        this.rebootQueueService = rebootQueueService;
        this.externalReboot = externalReboot;
        this.preCheck = preCheck;
    }


    @PostConstruct
    public void start() {
        executorService.submit(() ->
                {
                    while (!Thread.currentThread().isInterrupted()) {
                        int deviceId = 0;
                        try {
                            deviceId = rebootQueueService.takeFromQueue();

                            QueueResponse response = externalReboot.reboot(deviceId);

                            if (response == null) {
                                log.error("NOT");
                                continue;
                            }
                            if ("Success".equals(response.getStatus())) {
                                //    log.info("Rebooted");
                            } else {
                                log.error("Reboot Failed");
                            }

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            log.info("Reboot worker is interrupted. Stop working");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            preCheck.remove(deviceId);
                        }
                    }
                }

        );
    }
}
