package com.copaia.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID messageId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "conversation_id")
    private Conversation conversation;

    @Column(name = "from_ia")
    private boolean fromIa;

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFromIa() {
        return fromIa;
    }

    public void setFromIa(boolean fromIa) {
        this.fromIa = fromIa;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
