package com.copaia.dtos;

import java.util.UUID;

public record MessageDto(String content, boolean fromIa, UUID conversationId) {
}
