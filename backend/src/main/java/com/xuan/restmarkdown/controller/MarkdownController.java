package com.xuan.restmarkdown.controller;

import com.xuan.restmarkdown.generated.MarkdownRequest;
import com.xuan.restmarkdown.generated.MarkdownResponse;
import com.xuan.restmarkdown.service.MarkdownService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class MarkdownController {

    private final MarkdownService markdownService;

    @PostMapping("/markdown")
    public Mono<MarkdownResponse> generateMarkdown(@Valid @RequestBody MarkdownRequest request) {
        log.info("Received markdown generation request for prompt: {}", request.getPrompt());
        return markdownService.generateMarkdown(request);
    }
}
