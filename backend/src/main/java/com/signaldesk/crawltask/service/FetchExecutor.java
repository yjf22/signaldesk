package com.signaldesk.crawltask.service;

import com.signaldesk.source.domain.Source;
import com.signaldesk.source.domain.SourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Executes the raw HTTP fetch for URL and RSS sources.
 * PDF and NOTE sources return their existing content without network calls.
 */
@Component
public class FetchExecutor {

    private static final Logger log = LoggerFactory.getLogger(FetchExecutor.class);
    private final HttpClient httpClient;

    public FetchExecutor() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Fetch raw content from a source.
     *
     * @return FetchResult with raw content bytes and content type
     */
    public FetchResult fetch(Source source) {
        SourceType type = source.getSourceType();

        return switch (type) {
            case URL -> fetchUrl(source.getUrl());
            case RSS -> fetchUrl(source.getUrl());  // RSS is fetched as XML text
            case PDF -> fetchPdf(source.getUrl());
            case NOTE -> new FetchResult(source.getDescription() != null
                    ? source.getDescription().getBytes() : new byte[0],
                    "text/plain", 200);
        };
    }

    private FetchResult fetchUrl(String url) {
        if (url == null || url.isBlank()) {
            return new FetchResult(new byte[0], "text/plain", 400);
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("User-Agent", "SignalDesk/1.0 (Content Monitor)")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofByteArray());

            String contentType = response.headers().firstValue("Content-Type")
                    .orElse("text/html");

            return new FetchResult(response.body(), contentType, response.statusCode());
        } catch (Exception e) {
            log.error("Fetch failed for URL: {}", url, e);
            return new FetchResult(("Fetch error: " + e.getMessage()).getBytes(),
                    "text/plain", 500);
        }
    }

    private FetchResult fetchPdf(String url) {
        if (url == null || url.isBlank()) {
            return new FetchResult(new byte[0], "application/pdf", 400);
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(60))
                    .header("User-Agent", "SignalDesk/1.0 (Content Monitor)")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofByteArray());

            return new FetchResult(response.body(), "application/pdf", response.statusCode());
        } catch (Exception e) {
            log.error("PDF fetch failed for URL: {}", url, e);
            return new FetchResult(new byte[0], "application/pdf", 500);
        }
    }

    /**
     * Result of a fetch operation.
     */
    public record FetchResult(byte[] body, String contentType, int statusCode) {
        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public String bodyAsString() {
            return new String(body, java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}
