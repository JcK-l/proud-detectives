package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.ReadyMessage;
import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.messaging.MessageService;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReadyMessageServiceImpl implements MessageService {

    private final GameService gameService;

    public ReadyMessageServiceImpl(final GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.READY_MESSAGE.equals(messageType);
    }

    @Override
    public String handle(final Message message) {
        final ReadyMessage readyMessage = (ReadyMessage) message;
        final Game game = gameService.changeReadyStatus(readyMessage.getSenderId(), readyMessage.isReady());
        if (game == null) {
            return "TYPE:" + MessageType.READY_MESSAGE + ";status=418;gameId=null";
        } else {
            final Long gameId = game.getGameId();
            return "TYPE:" + MessageType.READY_MESSAGE + ";status=200;gameId=" + gameId +
                    ";players=" + getPlayerPseudonyms(game) +
                    ";playersReady=" + getReadyPlayersPseudonyms(game.getParticipants());
        }
    }

    private String getReadyPlayersPseudonyms(final List<Participant> participants) {
        return participants.stream()
                .filter(Participant::isReady)
                .map(Participant::getPlayer)
                .map(Player::getPseudonym)
                .collect(Collectors.joining(","));
    }
}
