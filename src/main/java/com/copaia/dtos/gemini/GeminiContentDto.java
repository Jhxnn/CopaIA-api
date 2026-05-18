package com.copaia.dtos.gemini;

import java.util.List;

public record GeminiContentDto(String role, List<GeminiPartDto> parts) {
}
