package org.example.controller;

import org.example.dto.BuildAnalysisRequest;
import org.example.dto.BuildAnalysisResponse;
import org.example.service.BuildAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analyze")
public class BuildAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(BuildAnalysisController.class);
    private static final int MAX_LOG_SIZE = 200_000; // 200 KB character limit for PoC

    private final BuildAnalysisService analysisService;

    public BuildAnalysisController(BuildAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> analyze(@RequestBody(required = false) BuildAnalysisRequest request) {
        if (request == null || request.getBuildLog() == null || request.getBuildLog().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "buildLog is required and must be non-empty"));
        }
        String buildLog = request.getBuildLog();
        if (buildLog.length() > MAX_LOG_SIZE) {
            logger.warn("Rejecting analyze request: payload too large ({} chars)", buildLog.length());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(Map.of("error", "buildLog exceeds maximum allowed size"));
        }

        // Log only metadata to avoid leaking sensitive content
        logger.info("Analyzing build log: length={} chars", buildLog.length());

        var resp = analysisService.analyze(buildLog);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp);
    }

    @PostMapping(path = "/extract", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> extract(@RequestBody(required = false) BuildAnalysisRequest request) {
        if (request == null || request.getBuildLog() == null || request.getBuildLog().isBlank()) {
            return ResponseEntity.badRequest().body("buildLog is required and must be non-empty");
        }
        String buildLog = request.getBuildLog();
        if (buildLog.length() > MAX_LOG_SIZE) {
            logger.warn("Rejecting extract request: payload too large ({} chars)", buildLog.length());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("buildLog exceeds maximum allowed size");
        }

        String snippet = analysisService.extractRelevantError(buildLog);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(snippet);
    }
}

