package com.external_api.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external/api")
public class ExternalController {


    @GetMapping("/reboot/{id}")
    public void rebootDevice(@PathVariable int id) {
        System.out.println("Device Rebooted:- " + id);
        try {
            Thread.sleep(500);

        } catch (Exception ignored) {
        }
    }

}
