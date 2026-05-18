package com.copaia.services;

import com.copaia.dtos.ConversationDto;
import com.copaia.dtos.ConversationResponseDto;
import com.copaia.dtos.MessageResponseDto;
import com.copaia.models.Conversation;
import com.copaia.models.Message;
import com.copaia.repositories.ConversationRepository;
import com.copaia.repositories.MessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationService(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public ConversationResponseDto create(ConversationDto dto) {
        Conversation conversation = new Conversation();
        conversation.setTitle(dto.title());
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        return toResponse(conversation, List.of());
    }

    public List<ConversationResponseDto> findAll() {
        return conversationRepository.findAllByOrderByUpdatedAtDesc().stream()
                .map(c -> toResponse(c, List.of()))
                .toList();
    }

    public ConversationResponseDto findById(UUID id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversa não encontrada"));

        List<Message> messages = messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
        return toResponse(conversation, messages);
    }

    public Conversation findEntityById(UUID id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversa não encontrada"));
    }

    public void updateTimestamp(Conversation conversation) {
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
    }

    private ConversationResponseDto toResponse(Conversation conversation, List<Message> messages) {
        List<MessageResponseDto> messageDtos = messages.stream()
                .map(m -> new MessageResponseDto(m.getMessageId(), m.getContent(), m.isFromIa(), m.getCreatedAt()))
                .toList();
        return new ConversationResponseDto(
                conversation.getConversationId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                messageDtos
        );
    }
}
