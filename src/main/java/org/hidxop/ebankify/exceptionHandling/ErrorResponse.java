package org.hidxop.ebankify.exceptionHandling;

import lombok.Builder;

import java.util.Map;

@Builder
public record ErrorResponse(
        String timestamp,
        Integer status,
        String error,
        String message,
        String path,
        Map<String, String> errors

) {
}
