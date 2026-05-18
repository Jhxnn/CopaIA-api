package com.copaia.services;

import com.copaia.dtos.MessageRequestDto;
import com.copaia.dtos.MessageResponseDto;
import com.copaia.models.Conversation;
import com.copaia.models.Message;
import com.copaia.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationService conversationService;
    private final IaService iaService;
    private final FootballService footballService;

    public MessageService(
            MessageRepository messageRepository,
            ConversationService conversationService,
            IaService iaService,
            FootballService footballService
    ) {
        this.messageRepository = messageRepository;
        this.conversationService = conversationService;
        this.iaService = iaService;
        this.footballService = footballService;
    }

    public List<MessageResponseDto> findByConversation(UUID conversationId) {
        Conversation conversation = conversationService.findEntityById(conversationId);
        return messageRepository.findByConversationOrderByCreatedAtAsc(conversation).stream()
                .map(m -> new MessageResponseDto(m.getMessageId(), m.getContent(), m.isFromIa(), m.getCreatedAt()))
                .toList();
    }

    public MessageResponseDto sendMessage(UUID conversationId, MessageRequestDto dto) {
        Conversation conversation = conversationService.findEntityById(conversationId);

        Message userMessage = new Message();
        userMessage.setContent(dto.content());
        userMessage.setConversation(conversation);
        userMessage.setFromIa(false);
        messageRepository.save(userMessage);

        List<Message> history = messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
        List<Message> historyWithoutLast = history.subList(0, history.size() - 1);

        String teamsContext = footballService.buildTeamsContext();
        String aiResponse = iaService.generateResponse(dto.content(), historyWithoutLast, teamsContext);

        Message aiMessage = new Message();
        aiMessage.setContent(aiResponse);
        aiMessage.setConversation(conversation);
        aiMessage.setFromIa(true);
        messageRepository.save(aiMessage);

        conversationService.updateTimestamp(conversation);

        return new MessageResponseDto(aiMessage.getMessageId(), aiResponse, true, aiMessage.getCreatedAt());
    }
}
