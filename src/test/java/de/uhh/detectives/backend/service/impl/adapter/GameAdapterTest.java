package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameAdapterTest {

    private final GameAdapter testee = new GameAdapter();

    @Test
    public void testSerialize() {
        // given
        final Player player1 = new Player();
        player1.setId(123L);
        final Player player2 = new Player();
        player2.setId(234L);
        final Player player3 = new Player();
        player3.setId(345L);
        final Player player4 = new Player();
        player4.setId(456L);

        final Long gameId = 11111111L;
        final Game game = new Game(gameId);
        game.setWeapon("Pistole");
        game.setLocation("Arbeitszimmer");
        game.setCulprit("Tom Gruen");
        game.setParticipants(Arrays.asList(player1, player2, player3, player4));

        // when
        final String actual = testee.serialize(game);

        // then
        assertEquals("gameId=11111111", actual);
    }
}
