package org.example.controller;

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
@RequestMapping("/analyze")
public class AnalyzeController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyzeController.class);
    private final BuildAnalysisService analysisService;

    public AnalyzeController(BuildAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> analyzePlain(@RequestBody(required = false) String buildLog) {
        ResponseEntity<?> validation = LogValidator.validate(buildLog, logger);
        if (validation != null) {
            return validation;
        }

        logger.info("Analyzing plain build log: length={} chars", buildLog.length());
        var response = analysisService.analyze(buildLog);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }
}
