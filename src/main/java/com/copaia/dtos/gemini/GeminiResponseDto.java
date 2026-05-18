package com.copaia.dtos.gemini;

import java.util.List;

public record GeminiResponseDto(List<GeminiCandidateDto> candidates) {
}
