package org.example.controller;

import org.example.service.BuildAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/build", ""})
public class BuildAnalysisController {

    private final BuildAnalysisService buildAnalysisService;

    @Autowired
    public BuildAnalysisController(BuildAnalysisService buildAnalysisService) {
        this.buildAnalysisService = buildAnalysisService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP");
    }

    @PostMapping("/extract")
    public String extractRelevantError(@RequestBody String buildLog) throws Exception {
        return buildAnalysisService.analyzeWithAI(buildLog, "build-log-context");
    }
}
