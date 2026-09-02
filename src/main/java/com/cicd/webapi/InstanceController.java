package com.cicd.webapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class InstanceController {

    @Value("${app.instance:BLUE}")
    private String instance;

    @Value("${server.port:8080}")
    private String port;

    @GetMapping("/api/instance")
    public Map<String, String> getInstance() {
        Map<String, String> response = new HashMap<>();
        response.put("instance", instance);
        response.put("port", port);
        return response;
    }
}
