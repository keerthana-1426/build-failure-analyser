package org.example.util;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class LogValidator {

    private LogValidator() {
    }

    private static final int MAX_LOG_SIZE = 200_000;
    private static final Logger LOGGER = LoggerFactory.getLogger(LogValidator.class);

    public static ResponseEntity<?> validate(String buildLog, Logger requestLogger) {
        if (buildLog == null || buildLog.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "buildLog is required and must be non-empty"));
        }
        if (buildLog.length() > MAX_LOG_SIZE) {
            Logger effectiveLogger = requestLogger == null ? LOGGER : requestLogger;
            effectiveLogger.warn("Rejecting request: payload too large ({} chars)", buildLog.length());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(Map.of("error", "buildLog exceeds maximum allowed size"));
        }
        return null;
    }
}
