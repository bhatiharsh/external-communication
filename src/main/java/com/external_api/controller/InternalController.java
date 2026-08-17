package com.external_api.controller;


import com.external_api.dto.QueueResponse;
import com.external_api.precheck.PreCheck;
import com.external_api.service.RebootQueueService;
import com.external_api.service.Service;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/Internal/api")
public class InternalController {


    @Autowired
    private Service service;


    @Autowired
    private RebootQueueService rebootQueueService;


    @Autowired
    private PreCheck preCheck;


    @GetMapping("/reboot/{id}")
    public ResponseEntity<?> deviceReboot(@PathVariable int id) {

        boolean added = preCheck.addId(id);


        if (!added) {
            System.out.println("Already in QUEUE Please wait Some Time:- " + id);

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Device Already queue or rebooting"
                    );

        }

        boolean queued = rebootQueueService.addToQueue(id);

        if (!queued) {
            return ResponseEntity.status(429).body(
                    Map.of(
                            "Device ID", id,
                            "Status", "OFF_LIMIT"
                    )
            );
        }

        return ResponseEntity.accepted().body(
                new QueueResponse(id, "Rebooting")
        );

    }

    @GetMapping("/method/load")
    public void loadApi() {
        System.out.println("started");
        for (int i = 0; i < 200; i++) {
            deviceReboot(i);
        }
        System.out.println("Stopped");
    }
}
