package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.entity.Game;
import org.springframework.stereotype.Component;

@Component
public class GameAdapter {

    public String serialize(final Game game) {
        // TODO implement serialization and return it
        return "gameId=" + game.getGameId().toString();
    }
}
