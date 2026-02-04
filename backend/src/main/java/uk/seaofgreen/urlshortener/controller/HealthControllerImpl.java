package uk.seaofgreen.urlshortener.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthControllerImpl implements HealthController {
    @Override
    @GetMapping("/health")
    public String health() { return "ok"; }
}
