package org.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BuildAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void healthEndpointReturnsUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("UP")));
    }

    @Test
    public void analyzeJsonDependencyDetected() throws Exception {
        String payload = "{\"buildLog\": \"[ERROR] Failed to execute goal: Could not find artifact com.example:missing:jar:1.0\"}";

        mockMvc.perform(post("/api/analyze").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorType").value("DEPENDENCY"))
                .andExpect(jsonPath("$.rootCause").exists())
                .andExpect(jsonPath("$.suggestedFix").exists());
    }

    @Test
    public void analyzeJsonEmptyBuildLogReturns400() throws Exception {
        String payload = "{\"buildLog\": \"  \"}";

        mockMvc.perform(post("/api/analyze").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("buildLog is required")));
    }

    @Test
    public void analyzeJsonOversizedReturns413() throws Exception {
        StringBuilder big = new StringBuilder();
        for (int i = 0; i < 220_000; i++) {
            big.append('a');
        }
        String payload = "{\"buildLog\": \"" + big + "\"}";

        mockMvc.perform(post("/api/analyze").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isPayloadTooLarge());
    }

    @Test
    public void analyzePlainAcceptsTextPlainAndReturnsJson() throws Exception {
        String body = "[ERROR] Compilation failure: cannot find symbol";

        mockMvc.perform(post("/analyze").contentType(MediaType.TEXT_PLAIN).content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorType").exists());
    }
}
