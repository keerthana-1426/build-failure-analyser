package org.example.controller;

import org.example.service.BuildAnalysisService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * Simple REST controller for the Build Failure Analyzer PoC.
 * Accepts a raw build log as plain text in the request body and returns the same content.
 * Controller delegates to a service layer for separation of concerns.
 */
@RestController
@RequestMapping("/analyze")
public class AnalyzeController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AnalyzeController.class);
    private static final int MAX_LOG_SIZE = 200_000;

    private final org.example.service.BuildAnalysisService analysisService;

    public AnalyzeController(org.example.service.BuildAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> analyzePlain(@RequestBody(required = false) String buildLog) {
        if (buildLog == null || buildLog.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "buildLog is required and must be non-empty"));
        }
        if (buildLog.length() > MAX_LOG_SIZE) {
            logger.warn("Rejecting analyze (plain) request: payload too large ({} chars)", buildLog.length());
            return ResponseEntity.status(org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(Map.of("error", "buildLog exceeds maximum allowed size"));
        }
        logger.info("Analyzing plain build log: length={} chars", buildLog.length());
        var resp = analysisService.analyze(buildLog);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(resp);
    }
}

