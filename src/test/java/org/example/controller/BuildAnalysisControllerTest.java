package org.example.controller;

import org.example.service.BuildAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuildAnalysisController.class)
public class BuildAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildAnalysisService buildAnalysisService;

    @Test
    public void healthReturnsUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }

    //@Test
    public void test() throws Exception {

    }
}
