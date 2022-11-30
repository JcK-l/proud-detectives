package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.messaging.JoinGameMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.RegisterMessage;
import de.uhh.detectives.backend.service.api.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        when(gameService.registerPlayer(eq(playerId))).thenReturn(new Game(111111111L));

        // when
        final String result = testee.handle(joinGameMessage);

        // then
        verify(gameService, times(0)).generateGame(anyLong());
        verify(gameService).registerPlayer(eq(playerId));
        assertEquals("TYPE:JOIN_GAME_MESSAGE;status=200;gameId=111111111", result);
    }

    @Test
    public void testHandleNoGameAvailable() {
        // given
        final Long playerId = 123456789L;
        final JoinGameMessage joinGameMessage = new JoinGameMessage();
        joinGameMessage.setSenderId(playerId);

        when(gameService.isJoinableGameAvailable()).thenReturn(false);
        when(gameService.registerPlayer(eq(playerId))).thenReturn(new Game(111111111L));

        // when
        final String result = testee.handle(joinGameMessage);

        // then
        verify(gameService).generateGame(anyLong());
        verify(gameService).registerPlayer(eq(playerId));
        assertEquals("TYPE:JOIN_GAME_MESSAGE;status=200;gameId=111111111", result);
    }

}
