package com.copaia.controllers;

import com.copaia.dtos.ConversationDto;
import com.copaia.dtos.ConversationResponseDto;
import com.copaia.services.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponseDto create(@RequestBody ConversationDto dto) {
        return conversationService.create(dto);
    }

    @GetMapping
    public List<ConversationResponseDto> findAll() {
        return conversationService.findAll();
    }

    @GetMapping("/{id}")
    public ConversationResponseDto findById(@PathVariable UUID id) {
        return conversationService.findById(id);
    }
}
