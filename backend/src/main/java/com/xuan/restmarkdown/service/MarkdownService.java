package com.xuan.restmarkdown.service;

import com.xuan.restmarkdown.generated.MarkdownRequest;
import com.xuan.restmarkdown.generated.MarkdownResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MarkdownService {

    public Mono<MarkdownResponse> generateMarkdown(MarkdownRequest request) {
        log.debug("Generating markdown for prompt: {}", request.getPrompt());
        MarkdownResponse response = new MarkdownResponse();
        response.setMarkdown(buildFallbackMarkdown(request.getPrompt()));
        return Mono.just(response);
    }

    private String buildFallbackMarkdown(String prompt) {
        return """
                # REST Markdown

                ## Prompt

                %s

                ## Example Request

                ```http
                GET /api/health
                ```
                """.formatted(prompt);
    }
}
