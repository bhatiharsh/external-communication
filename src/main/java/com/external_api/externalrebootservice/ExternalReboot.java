package com.external_api.externalrebootservice;

import com.external_api.dto.QueueResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalReboot {


    private final RestTemplate restTemplate;


    public ExternalReboot(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public QueueResponse reboot(int id) {
        String url = "http://localhost:9191/external/api/reboot/" + id;
        restTemplate.getForObject(
                url,
                Integer.class,
                id
        );

        return new QueueResponse(
                id,
                "Success"
        );
    }
}
