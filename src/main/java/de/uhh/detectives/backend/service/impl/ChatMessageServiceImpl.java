package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.ChatMessage;
import de.uhh.detectives.backend.repository.ChatMessageRepository;
import de.uhh.detectives.backend.service.api.ChatMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    private ChatMessageRepository chatMessageRepository;

    public ChatMessageServiceImpl(final ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public void saveMessage(final ChatMessage message) {
        LOG.info("persist chat message into database");
        chatMessageRepository.save(message);
    }
}
