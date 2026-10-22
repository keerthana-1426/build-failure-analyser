package org.example.controller;

import org.example.dto.BuildAnalysisRequest;
import org.example.service.BuildAnalysisService;
import org.example.util.LogValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analyze")
public class BuildAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(BuildAnalysisController.class);
    private final BuildAnalysisService analysisService;

    public BuildAnalysisController(BuildAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> analyze(@RequestBody(required = false) BuildAnalysisRequest request) {
        String buildLog = request == null ? null : request.getBuildLog();
        ResponseEntity<?> validation = LogValidator.validate(buildLog, logger);
        if (validation != null) {
            return validation;
        }

        logger.info("Analyzing build log: length={} chars", buildLog.length());
        var response = analysisService.analyze(buildLog);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @PostMapping(path = "/extract", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> extract(@RequestBody(required = false) BuildAnalysisRequest request) {
        String buildLog = request == null ? null : request.getBuildLog();
        ResponseEntity<?> validation = LogValidator.validate(buildLog, logger);
        if (validation != null) {
            return validation;
        }

        String snippet = analysisService.extractRelevantError(buildLog);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(snippet);
    }
}
