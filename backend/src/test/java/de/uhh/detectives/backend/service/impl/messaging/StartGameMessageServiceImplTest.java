package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.messaging.JoinGameMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.StartGameMessage;
import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.impl.adapter.GameAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StartGameMessageServiceImplTest {

    @InjectMocks
    private StartGameMessageServiceImpl testee;

    @Mock
    private GameService gameService;

    @Mock
    private GameAdapter gameAdapter;

    @Test
    public void testAccepts() {
        final Message startGameMessage = new StartGameMessage();
        assertTrue(testee.accepts(startGameMessage.getType()));
    }

    @Test
    public void testNotAccepts() {
        final Message joinGameMessage = new JoinGameMessage();
        assertFalse(testee.accepts(joinGameMessage.getType()));
    }

    @Test
    public void testHandleNoGameFoundToStart(){
        // given
        final Long playerId = 123456789L;
        final Double playerLongitude = -0.158670367d;
        final Double playerLatitude = 51.52406527d;
        final Integer playingFieldSize = 500;
        final StartGameMessage startGameMessage = new StartGameMessage();
        startGameMessage.setUserId(playerId);
        startGameMessage.setLongitude(playerLongitude);
        startGameMessage.setLatitude(playerLatitude);
        startGameMessage.setRadius(playingFieldSize);

        when(gameService.startGame(eq(playerId), eq(playerLongitude), eq(playerLatitude), eq(playingFieldSize))).thenReturn(null);

        // when
        final String result = testee.handle(startGameMessage);

        // then
        verify(gameService).startGame(eq(playerId), eq(playerLongitude), eq(playerLatitude), eq(playingFieldSize));
        assertEquals("TYPE:START_GAME_MESSAGE;status=418;gameId=null", result);
    }

    @Test
    public void testHandle(){
        // given
        final Long playerId = 123456789L;
        final Double playerLongitude = -0.158670367d;
        final Double playerLatitude = 51.52406527d;
        final StartGameMessage startGameMessage = new StartGameMessage();
        startGameMessage.setUserId(playerId);
        startGameMessage.setLongitude(playerLongitude);
        startGameMessage.setLatitude(playerLatitude);

        final Game game = new Game(111111222222L);
        when(gameService.startGame(eq(playerId), eq(playerLongitude), eq(playerLatitude), isNull())).thenReturn(game);
        when(gameAdapter.serialize(game)).thenReturn("gameId=111111222222");

        // when
        final String result = testee.handle(startGameMessage);

        // then
        verify(gameService).startGame(eq(playerId), eq(playerLongitude), eq(playerLatitude), isNull());
        assertEquals("TYPE:START_GAME_MESSAGE;status=200;gameId=111111222222", result);
    }
}
