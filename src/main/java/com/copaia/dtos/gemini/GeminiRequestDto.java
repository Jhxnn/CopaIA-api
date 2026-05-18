package com.copaia.dtos.gemini;

import java.util.List;

public record GeminiRequestDto(
        List<GeminiContentDto> contents,
        GeminiSystemInstructionDto systemInstruction,
        GeminiGenerationConfigDto generationConfig
) {
}
