package com.signaldesk.summary.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Component
public class OpenAIClient implements AIClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAIClient.class);
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final int timeoutSeconds;

    public OpenAIClient(
            @Value("${ai.openai.api-key:}") String apiKey,
            @Value("${ai.openai.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${ai.openai.model:gpt-3.5-turbo}") String model,
            @Value("${ai.openai.timeout-seconds:60}") int timeoutSeconds) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
        this.timeoutSeconds = timeoutSeconds;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AIResponse generate(String systemPrompt, String userContent) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("OpenAI API key not configured. Returning mock response.");
            return new AIResponse(
                    "AI summary service not configured. Please set OPENAI_API_KEY environment variable.\n\n"
                    + "Content preview:\n" + truncate(userContent, 500),
                    "mock", 0, 0);
        }

        try {
            ChatCompletionRequest request = new ChatCompletionRequest(
                    model,
                    List.of(
                            new Message("system", systemPrompt),
                            new Message("user", userContent)
                    ),
                    0.3,
                    2000
            );

            String requestBody = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/chat/completions"))
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ChatCompletionResponse completion = objectMapper.readValue(
                        response.body(), ChatCompletionResponse.class);

                String content = completion.choices().get(0).message().content();
                int promptTokens = completion.usage() != null ? completion.usage().promptTokens() : 0;
                int completionTokens = completion.usage() != null ? completion.usage().completionTokens() : 0;

                return new AIResponse(content, model, promptTokens, completionTokens);
            } else {
                log.error("OpenAI API error: {} - {}", response.statusCode(), response.body());
                throw new RuntimeException("OpenAI API returned status: " + response.statusCode());
            }

        } catch (Exception e) {
            log.error("OpenAI API call failed", e);
            throw new RuntimeException("AI service error: " + e.getMessage(), e);
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }

    // ---- JSON mapping records ----

    record ChatCompletionRequest(
            String model,
            List<Message> messages,
            double temperature,
            @JsonProperty("max_tokens") int maxTokens
    ) {}

    record Message(String role, String content) {}

    record ChatCompletionResponse(
            List<Choice> choices,
            Usage usage
    ) {}

    record Choice(Message message) {}

    record Usage(
            @JsonProperty("prompt_tokens") int promptTokens,
            @JsonProperty("completion_tokens") int completionTokens,
            @JsonProperty("total_tokens") int totalTokens
    ) {}
}
