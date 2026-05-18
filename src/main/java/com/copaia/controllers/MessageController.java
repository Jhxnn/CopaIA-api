package com.copaia.controllers;

import com.copaia.dtos.MessageRequestDto;
import com.copaia.dtos.MessageResponseDto;
import com.copaia.services.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<MessageResponseDto> findAll(@PathVariable UUID conversationId) {
        return messageService.findByConversation(conversationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDto send(@PathVariable UUID conversationId, @RequestBody MessageRequestDto dto) {
        return messageService.sendMessage(conversationId, dto);
    }
}
