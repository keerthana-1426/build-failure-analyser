package org.example.controller;

import org.example.service.BuildAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analyze")
public class AnalyzeController {

    private final BuildAnalysisService buildAnalysisService;

    @Autowired
    public AnalyzeController(BuildAnalysisService buildAnalysisService) {
        this.buildAnalysisService = buildAnalysisService;
    }

    @PostMapping
    public String analyze(@RequestBody String snippet) throws Exception {
        return buildAnalysisService.analyzeWithAI(snippet, "default-context");
    }
}
