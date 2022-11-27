package de.uhh.detectives.backend.service.api;

import de.uhh.detectives.backend.model.ChatMessage;

public interface ChatMessageService {
    void saveMessage(final ChatMessage message);
}
