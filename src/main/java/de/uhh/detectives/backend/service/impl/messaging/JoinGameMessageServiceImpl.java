package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.messaging.JoinGameMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.messaging.MessageService;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Service;

@Service
public class JoinGameMessageServiceImpl implements MessageService {

    private final GameService gameService;

    public JoinGameMessageServiceImpl(final GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.JOIN_GAME_MESSAGE.equals(messageType);
    }

    @Override
    public String handle(final Message message) {
        final JoinGameMessage joinGameMessage = (JoinGameMessage) message;
        final boolean joinableGameAvailable = gameService.isJoinableGameAvailable();
        if (!joinableGameAvailable) {
            gameService.generateGame(System.currentTimeMillis());
        }
        final Game game = gameService.registerPlayer(joinGameMessage.getSenderId());
        if (game == null) {
            return "TYPE:" + MessageType.JOIN_GAME_MESSAGE + ";status=418;gameId=null";
        } else {
            final Long gameId = game.getGameId();
            return "TYPE:" + MessageType.JOIN_GAME_MESSAGE + ";status=200;gameId=" + gameId;
        }
    }
}
