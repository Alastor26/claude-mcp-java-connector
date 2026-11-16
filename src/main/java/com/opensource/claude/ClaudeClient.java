package com.opensource.claude;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ClaudeClient {

    private static final String BASE_URL = "https://anthropic.com";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ClaudeClient(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Anthropic API key must not be null or empty.");
        }
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Sends a message to Claude and captures a metadata-only TelemetryEvent from the API response.
     */
    public TelemetryStarter.TelemetryEvent sendMessageAndCaptureTelemetry(
            String model,
            String prompt,
            int maxTokens
    ) throws IOException, InterruptedException {

        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("model", model);
        payload.put("max_tokens", maxTokens);

        ArrayNode messages = payload.putArray("messages");
        ObjectNode userMessage = messages.addObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/messages"))
                .header("x-api-key", apiKey)
                .header("anthropic-version", ANTHROPIC_VERSION)
                .header("content-type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Anthropic API error (" + response.statusCode() + "): " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode usageNode = root.path("usage");

        int inputTokens = usageNode.path("input_tokens").asInt(0);
        int outputTokens = usageNode.path("output_tokens").asInt(0);
        String resolvedModel = root.path("model").asText(model);

        return TelemetryStarter.createEvent(resolvedModel, inputTokens, outputTokens, true);
    }

    /**
     * Fetches organizational usage metrics (requires an Anthropic Admin API key: sk-ant-admin-...).
     */
    public JsonNode fetchOrganizationUsageReport(String startingAtIso, String endingAtIso)
            throws IOException, InterruptedException {

        String uri = String.format(
                "%s/organizations/usage_report/messages?starting_at=%s&ending_at=%s",
                BASE_URL, startingAtIso, endingAtIso
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("x-api-key", apiKey)
                .header("anthropic-version", ANTHROPIC_VERSION)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Organization API error (" + response.statusCode() + "): " + response.body());
        }

        return objectMapper.readTree(response.body());
    }
}
