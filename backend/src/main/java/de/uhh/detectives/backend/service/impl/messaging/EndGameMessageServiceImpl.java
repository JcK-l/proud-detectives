package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.messaging.EndGameMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.messaging.MessageService;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EndGameMessageServiceImpl implements MessageService {

    private final GameService gameService;

    public EndGameMessageServiceImpl(final GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.END_GAME_MESSAGE.equals(messageType);
    }

    @Override
    public String handle(final Message message) {
        final EndGameMessage endGameMessage = (EndGameMessage) message;
        final Long userId = endGameMessage.getSenderId();
        final boolean win = endGameMessage.isWin();
        final Game game = gameService.endGame(userId, win);
        if (game == null) {
            return "TYPE:" + MessageType.END_GAME_MESSAGE + ";status=418;gameId=null";
        }
        final Long gameId = game.getGameId();
        if (!win && everyoneLost(game)) {
            return "TYPE:" + MessageType.END_GAME_MESSAGE + ";status=200;gameId=" + gameId + ";winnerId=null;winnerPseudonym=null";
        }
        if (!win) {
            return null;
        }
        final String winnerPseudonym = getWinnerPseudonym(game, userId);
        return "TYPE:" + MessageType.END_GAME_MESSAGE + ";status=200;gameId=" + gameId + ";winnerId=" + userId
                + ";winnerPseudonym=" + winnerPseudonym;
    }

    private String getWinnerPseudonym(final Game game, final Long winnerId) {
        final List<Player> participants = game.getParticipants().stream().map(Participant::getPlayer).toList();
        final Optional<Player> winner = participants.stream().filter(p -> winnerId.equals(p.getId())).findFirst();
        return winner.isEmpty() ? null : winner.get().getPseudonym();
    }

    private boolean everyoneLost(final Game game) {
        return game.getParticipants().stream()
                .allMatch(Participant::isLost);
    }
}
