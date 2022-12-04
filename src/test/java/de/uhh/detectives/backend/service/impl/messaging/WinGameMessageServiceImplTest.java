package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.messaging.JoinGameMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.WinGameMessage;
import de.uhh.detectives.backend.service.api.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WinGameMessageServiceImplTest {

    @InjectMocks
    private WinGameMessageServiceImpl testee;

    @Mock
    private GameService gameService;

    @Test
    public void testAccepts() {
        final Message message = new WinGameMessage();
        assertTrue(testee.accepts(message.getType()));
    }

    @Test
    public void testNotAccepts() {
        final Message message = new JoinGameMessage();
        assertFalse(testee.accepts(message.getType()));
    }

    @Test
    public void testHandleNoGameFound(){
        // given
        final Long playerId = 123456789L;
        final WinGameMessage winGameMessage = new WinGameMessage();
        winGameMessage.setSenderId(playerId);
        when(gameService.endGame(eq(playerId))).thenReturn(null);

        // when
        final String result = testee.handle(winGameMessage);

        // then
        verify(gameService).endGame(eq(playerId));
        assertEquals("TYPE:WIN_GAME_MESSAGE;status=418;gameId=null", result);
    }

    @Test
    public void testHandle(){
        // given
        final Long playerId = 123456789L;
        final WinGameMessage winGameMessage = new WinGameMessage();
        winGameMessage.setSenderId(playerId);

        final Player player1 = new Player();
        player1.setId(1L);
        final Player player2 = new Player();
        player2.setId(2L);
        final Player player3 = new Player();
        player3.setId(playerId);
        player3.setPseudonym("Mr. Tester");
        final Player player4 = new Player();
        player4.setId(4L);

        final Long gameId = 11111111L;
        final Game game = new Game(gameId);
        game.setWinnerId(playerId);
        game.setParticipants(Arrays.asList(player1, player2, player3, player4));

        when(gameService.endGame(eq(playerId))).thenReturn(game);

        // when
        final String result = testee.handle(winGameMessage);

        // then
        verify(gameService).endGame(eq(playerId));
        assertEquals("TYPE:WIN_GAME_MESSAGE;status=200;gameId=11111111;winnerId=123456789;winnerPseudonym=Mr. Tester", result);
    }
}
