package com.opensource.claude;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;

public class TelemetryStarter {

    public record TokenUsage(int inputTokens, int outputTokens, int totalTokens) {}

    public record TelemetryEvent(
            Instant timestamp,
            String provider,
            String model,
            TokenUsage usage,
            String privacyMode
    ) {}

    public static TelemetryEvent createEvent(String model, int inputTokens, int outputTokens, boolean metadataOnly) {
        TokenUsage usage = new TokenUsage(inputTokens, outputTokens, inputTokens + outputTokens);
        String mode = metadataOnly ? "metadata_only" : "full_payload";
        return new TelemetryEvent(Instant.now(), "anthropic", model, usage, mode);
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);

        TelemetryEvent sampleEvent = createEvent("claude-3-7-sonnet-latest", 420, 185, true);
        System.out.println(mapper.writeValueAsString(sampleEvent));
    }
}
