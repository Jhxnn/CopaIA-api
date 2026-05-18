package com.copaia.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ConversationResponseDto(
        UUID conversationId,
        String title,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<MessageResponseDto> messages
) {
}
