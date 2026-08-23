package com.external_api.controller;


import com.external_api.dto.QueueResponse;
import com.external_api.precheck.PreCheck;
import com.external_api.rabbitmq.ConfigMQ;
import com.external_api.service.RebootQueueService;
import com.external_api.service.Service;
import org.apache.coyote.Response;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
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

    @Autowired
    private RabbitTemplate RabbitTemplate;

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

    @GetMapping("/without-queue/{id}")
    public void withoutQueueCode(@PathVariable int id) {
        service.rebootSlow(id);
    }

    @GetMapping("/method/load")
    public void loadApi() {
        long start = System.currentTimeMillis();
        System.out.println("started");

        for (int i = 0; i < 200; i++) {
            deviceReboot(i);
        }

        long end = System.currentTimeMillis();

        System.out.println(end - start / 600);
        System.out.println("Stopped");
    }


    @GetMapping("/method/load2")
    public void loadApI() {
        long start = System.currentTimeMillis();
        System.out.println("started");
        for (int i = 0; i < 50; i++) {
            withoutQueueCode(i);
        }
        long end = System.currentTimeMillis();

        System.out.println(end - start / 600);

        System.out.println("Stopped");
    }


    @GetMapping("/sent-mail/{id}")
    public void sentMail(@PathVariable int id) {
        // for (int i = 0; i < 100; i++)

        System.out.println("Publishing message to RabbitMQ");

        RabbitTemplate.convertAndSend(
                ConfigMQ.MAIL_EXCHANGE,
                ConfigMQ.MAIL_ROUTING_KEY, "Hello");

        System.out.println("Message published");


    }


}
