package org.example.dto;

/**
 * DTO containing analysis result information.
 */
public class BuildAnalysisResponse {
    private String rootCause;
    private String errorType;
    private String suggestedFix;
    private String confidence;
    private String note;

    public BuildAnalysisResponse() {
    }

    public BuildAnalysisResponse(String rootCause, String errorType, String suggestedFix, String confidence, String note) {
        this.rootCause = rootCause;
        this.errorType = errorType;
        this.suggestedFix = suggestedFix;
        this.confidence = confidence;
        this.note = note;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getSuggestedFix() {
        return suggestedFix;
    }

    public void setSuggestedFix(String suggestedFix) {
        this.suggestedFix = suggestedFix;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

