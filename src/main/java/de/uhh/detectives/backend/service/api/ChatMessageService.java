package de.uhh.detectives.backend.service.api;

import de.uhh.detectives.backend.model.entity.ChatMessage;

public interface ChatMessageService extends MessageService {
    void saveMessage(final ChatMessage message);
}
