package org.example.dto;


public class BuildAnalysisRequest {

    private String buildLog;

    public BuildAnalysisRequest() {
    }

    public BuildAnalysisRequest(String buildLog) {
        this.buildLog = buildLog;
    }

    public String getBuildLog() {
        return buildLog;
    }

    public void setBuildLog(String buildLog) {
        this.buildLog = buildLog;
    }
}

