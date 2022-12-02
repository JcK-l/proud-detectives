package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.StartGameMessage;
import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.messaging.MessageService;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import de.uhh.detectives.backend.service.impl.adapter.GameAdapter;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StartGameMessageServiceImpl implements MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(StartGameMessageServiceImpl.class);

    private final GameService gameService;
    private final GameAdapter gameAdapter;

    public StartGameMessageServiceImpl(final GameService gameService, final GameAdapter gameAdapter) {
        this.gameService = gameService;
        this.gameAdapter = gameAdapter;
    }

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.START_GAME_MESSAGE.equals(messageType);
    }

    @Override
    public String handle(final Message message) {
        final StartGameMessage startGameMessage = (StartGameMessage) message;
        final Long userId = startGameMessage.getUserId();
        final Double latitude = startGameMessage.getLatitude();
        final Double longitude = startGameMessage.getLongitude();
        final Game game = gameService.startGame(userId, longitude, latitude);
        if (game == null) {
            return "TYPE:" + MessageType.START_GAME_MESSAGE + ";status=418;gameId=null";
        } else {
            final String players = getPlayerPseudonyms(game.getParticipants());
            LOG.info(String.format("Started game with ID %d with players %s.", game.getGameId(), players));
            return "TYPE:" + MessageType.START_GAME_MESSAGE + ";status=200;" + gameAdapter.serialize(game);
        }
    }

    private String getPlayerPseudonyms(final List<Player> players) {
        return Strings.join(players, ',');
    }
}
