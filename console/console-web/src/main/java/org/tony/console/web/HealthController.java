package org.tony.console.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/status")
    public String status() {
        return "ok";
    }

    @GetMapping("/prometheus")
    public String prometheus() {
        return "ok";
    }

}
