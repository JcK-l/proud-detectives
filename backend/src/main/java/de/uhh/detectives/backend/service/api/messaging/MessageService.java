package de.uhh.detectives.backend.service.api.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.messaging.Message;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

public interface MessageService {

    boolean accepts(final MessageType messageType);
    String handle(final Message message);

    default String getPlayerPseudonyms(final Game game) {
        final List<Player> players = game.getParticipants().stream().map(Participant::getPlayer).toList();
        final List<String> pseudonyms = players.stream().map(Player::getPseudonym).toList();
        return Strings.join(pseudonyms, ',');
    }
}
