package de.uhh.detectives.backend.repository;

import de.uhh.detectives.backend.model.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}