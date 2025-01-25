package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.entity.ChatMessage;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.repository.ChatMessageRepository;
import de.uhh.detectives.backend.repository.PlayerRepository;
import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.messaging.MessageService;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import de.uhh.detectives.backend.service.impl.adapter.ChatMessageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ChatMessageServiceImpl implements MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    private final ChatMessageRepository chatMessageRepository;
    private final PlayerRepository playerRepository;

    private final GameService gameService;

    private final ChatMessageAdapter chatMessageAdapter;

    public ChatMessageServiceImpl(final ChatMessageRepository chatMessageRepository,
                                  final PlayerRepository playerRepository, final GameService gameService) {
        this.chatMessageRepository = chatMessageRepository;
        this.playerRepository = playerRepository;
        this.chatMessageAdapter = new ChatMessageAdapter();
        this.gameService = gameService;
    }

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.CHAT_MESSAGE.equals(messageType);
    }

    @Override
    public String handle(final Message message) {
        String result;
        final ChatMessage chatMessage = (ChatMessage) message;

        Long senderId = chatMessage.getSenderId();
        LOG.info("persist chat message into database");
        chatMessageRepository.save(chatMessage);

        final Optional<Player> sender = playerRepository.findById(senderId);
        sender.ifPresent(player -> chatMessage.setSenderPseudonym(player.getPseudonym()));

        Game gameForUser = gameService.findActiveGameForUser(senderId);
        boolean senderHasLost = gameForUser.getParticipants().stream()
                .filter(p -> Objects.equals(p.getPlayer().getId(), senderId))
                .anyMatch(Participant::isLost);
        result = chatMessageAdapter.toBroadcastString(chatMessage) + ";dead=" + senderHasLost;
        return result;
    }

}
