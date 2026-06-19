package com.signaldesk.summary.client;

/**
 * Abstraction for AI summarization providers.
 */
public interface AIClient {

    /**
     * Generate a summary of the given content.
     *
     * @param systemPrompt  system-level instruction
     * @param userContent   the content to summarize
     * @return AIResponse containing the generated text and token usage
     */
    AIResponse generate(String systemPrompt, String userContent);

    record AIResponse(String content, String model, int promptTokens, int completionTokens) {}
}
