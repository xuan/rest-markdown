package com.xuan.restmarkdown;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = com.xuan.restmarkdown.RestMarkdownApplication.class)
@AutoConfigureMockMvc
class MarkdownControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGenerateMarkdownFromPrompt() throws Exception {
        mockMvc.perform(post("/api/markdown")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"prompt\":\"Create a user endpoint\"}"))
                .andExpect(status().isOk());
    }
}
