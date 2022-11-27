package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.ChatMessage;
import de.uhh.detectives.backend.repository.ChatMessageRepository;
import de.uhh.detectives.backend.service.api.ChatMessageService;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private ChatMessageRepository chatMessageRepository;

    public ChatMessageServiceImpl(final ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void saveMessage(final ChatMessage message) {
        chatMessageRepository.save(message);
    }
}
