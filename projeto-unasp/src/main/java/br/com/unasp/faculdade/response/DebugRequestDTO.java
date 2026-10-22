package br.com.unasp.faculdade.response;

public record DebugRequestDTO(
        String id,
        String timestamp,
        String method,
        String path,
        int status,
        String statusText,
        long durationMs,
        String requestBody,
        String responseBody,
        String column
) {
}
