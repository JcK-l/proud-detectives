package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.entity.ChatMessage;
import de.uhh.detectives.backend.model.Message;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.repository.ChatMessageRepository;
import de.uhh.detectives.backend.repository.PlayerRepository;
import de.uhh.detectives.backend.service.api.ChatMessageService;
import de.uhh.detectives.backend.service.api.MessageType;
import de.uhh.detectives.backend.service.impl.adapter.ChatMessageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    private final ChatMessageRepository chatMessageRepository;
    private final PlayerRepository playerRepository;

    private final ChatMessageAdapter chatMessageAdapter;

    public ChatMessageServiceImpl(final ChatMessageRepository chatMessageRepository, final PlayerRepository playerRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.playerRepository = playerRepository;
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
        final Optional<Player> sender = playerRepository.findById(chatMessage.getSenderId());
        sender.ifPresent(player -> chatMessage.setSenderPseudonym(player.getPseudonym()));
        return chatMessageAdapter.toBroadcastString(chatMessage);
    }

    @Override
    public void saveMessage(final ChatMessage message) {
        LOG.info("persist chat message into database");
        chatMessageRepository.save(message);
    }
}
