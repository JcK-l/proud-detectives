package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.messaging.JoinGameMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.RegisterMessage;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JoinGameMessageServiceImplTest {

    @InjectMocks
    private JoinGameMessageServiceImpl testee;

    @Mock
    private GameService gameService;

    @Test
    public void testAccepts() {
        final Message message = new JoinGameMessage();
        assertTrue(testee.accepts(message.getType()));
    }

    @Test
    public void testNotAccepts() {
        final Message message = new RegisterMessage();
        assertFalse(testee.accepts(message.getType()));
    }

    @Test
    public void testHandleGameAvailable() {
        // given
        final Long playerId = 123456789L;
        final JoinGameMessage joinGameMessage = new JoinGameMessage();
        joinGameMessage.setSenderId(playerId);

        when(gameService.isJoinableGameAvailable()).thenReturn(true);

        final Game game = new Game(111111111L);
        final Player player1 = new Player();
        player1.setId(playerId);
        player1.setPseudonym("Alice");
        final Player player2 = new Player();
        player2.setId(playerId);
        player2.setPseudonym("Bob");
        game.setParticipants(Arrays.asList(player1, player2));

        when(gameService.registerPlayer(eq(playerId))).thenReturn(game);

        // when
        final String result = testee.handle(joinGameMessage);

        // then
        verify(gameService, times(0)).generateGame(anyLong());
        verify(gameService).registerPlayer(eq(playerId));
        assertEquals("TYPE:JOIN_GAME_MESSAGE;status=200;gameId=111111111;players=Alice,Bob", result);
    }

    @Test
    public void testHandleNoGameAvailable() {
        // given
        final Long playerId = 123456789L;
        final JoinGameMessage joinGameMessage = new JoinGameMessage();
        joinGameMessage.setSenderId(playerId);

        when(gameService.isJoinableGameAvailable()).thenReturn(false);

        final Game game = new Game(111111111L);
        final Player player1 = new Player();
        player1.setId(playerId);
        player1.setPseudonym("Alice");
        final Player player2 = new Player();
        player2.setId(playerId);
        player2.setPseudonym("Bob");
        game.setParticipants(Arrays.asList(player1, player2));

        when(gameService.registerPlayer(eq(playerId))).thenReturn(game);

        // when
        final String result = testee.handle(joinGameMessage);

        // then
        verify(gameService).generateGame(anyLong());
        verify(gameService).registerPlayer(eq(playerId));
        assertEquals("TYPE:JOIN_GAME_MESSAGE;status=200;gameId=111111111;players=Alice,Bob", result);
    }

    @Test
    public void testHandlePlayerAlreadyInGame() {
        // given
        final Long playerId = 123456789L;
        final JoinGameMessage joinGameMessage = new JoinGameMessage();
        joinGameMessage.setSenderId(playerId);

        when(gameService.isJoinableGameAvailable()).thenReturn(true);
        when(gameService.registerPlayer(eq(playerId))).thenReturn(null);

        // when
        final String result = testee.handle(joinGameMessage);

        // then
        verify(gameService).registerPlayer(eq(playerId));
        assertEquals("TYPE:JOIN_GAME_MESSAGE;status=418;gameId=null", result);
    }

}
