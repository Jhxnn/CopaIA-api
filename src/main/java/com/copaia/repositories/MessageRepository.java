package com.copaia.repositories;

import com.copaia.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}
