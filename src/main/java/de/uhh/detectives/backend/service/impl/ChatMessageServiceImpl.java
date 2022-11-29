package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.entity.ChatMessage;
import de.uhh.detectives.backend.model.Message;
import de.uhh.detectives.backend.repository.ChatMessageRepository;
import de.uhh.detectives.backend.service.api.ChatMessageService;
import de.uhh.detectives.backend.service.api.MessageType;
import de.uhh.detectives.backend.service.impl.adapter.ChatMessageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageAdapter chatMessageAdapter;

    public ChatMessageServiceImpl(final ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageAdapter = new ChatMessageAdapter();
    }

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.CHAT_MESSAGE.equals(messageType);
    }

    @Override
    public String handle(final Message message) {
        final ChatMessage chatMessage = (ChatMessage) message;
        saveMessage(chatMessage);
        return chatMessageAdapter.toBroadcastString(chatMessage);
    }

    @Override
    public void saveMessage(final ChatMessage message) {
        LOG.info("persist chat message into database");
        chatMessageRepository.save(message);
    }
}
