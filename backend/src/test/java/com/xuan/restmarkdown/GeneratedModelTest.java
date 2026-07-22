package com.xuan.restmarkdown;

import com.xuan.restmarkdown.generated.MarkdownRequest;
import com.xuan.restmarkdown.generated.MarkdownResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedModelTest {

    @Test
    void generatedDtosShouldSupportLombokBuilder() {
        MarkdownRequest request = MarkdownRequest.builder()
                .prompt("Create a user endpoint")
                .build();

        assertThat(request.getPrompt()).isEqualTo("Create a user endpoint");

        MarkdownResponse response = MarkdownResponse.builder()
                .markdown("# User endpoint")
                .build();

        assertThat(response.getMarkdown()).isEqualTo("# User endpoint");
    }
}
