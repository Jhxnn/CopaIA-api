package com.copaia.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponseDto(UUID messageId, String content, boolean fromIa, LocalDateTime createdAt) {
}
