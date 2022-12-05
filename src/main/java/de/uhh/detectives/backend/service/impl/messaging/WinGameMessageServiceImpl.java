package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.WinGameMessage;
import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.messaging.MessageService;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WinGameMessageServiceImpl implements MessageService {

    private final GameService gameService;

    public WinGameMessageServiceImpl(final GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.WIN_GAME_MESSAGE.equals(messageType);
    }

    @Override
    public String handle(final Message message) {
        final WinGameMessage winGameMessage = (WinGameMessage) message;
        final Long winnerId = winGameMessage.getSenderId();
        final Game game = gameService.endGame(winnerId);
        if (game == null || getWinnerPseudonym(game, winnerId) == null) {
            return "TYPE:" + MessageType.WIN_GAME_MESSAGE + ";status=418;gameId=null";
        } else {
            final Long gameId = game.getGameId();
            final String winnerPseudonym = getWinnerPseudonym(game, winnerId);
            return "TYPE:" + MessageType.WIN_GAME_MESSAGE + ";status=200;gameId=" + gameId + ";winnerId=" + winnerId
                    + ";winnerPseudonym=" + winnerPseudonym;
        }
    }

    private String getWinnerPseudonym(final Game game, final Long winnerId) {
        final List<Player> participants = game.getParticipants().stream().map(Participant::getPlayer).toList();
        final Optional<Player> winner = participants.stream().filter(p -> winnerId.equals(p.getId())).findFirst();
        return winner.isEmpty() ? null : winner.get().getPseudonym();
    }
}
