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
        final Float playerLongitude = -0.158670367f;
        final Float playerLatitude = 51.52406527f;
        final StartGameMessage startGameMessage = new StartGameMessage();
        startGameMessage.setUserId(playerId);
        startGameMessage.setLongitude(playerLongitude);
        startGameMessage.setLatitude(playerLatitude);

        when(gameService.startGame(eq(playerId), eq(playerLongitude), eq(playerLatitude))).thenReturn(null);

        // when
        final String result = testee.handle(startGameMessage);

        // then
        verify(gameService).startGame(eq(playerId), eq(playerLongitude), eq(playerLatitude));
        assertEquals("TYPE:START_GAME_MESSAGE;status=418;gameId=null", result);
    }

    @Test
    public void testHandle(){
        // given
        final Long playerId = 123456789L;
        final Float playerLongitude = -0.158670367f;
        final Float playerLatitude = 51.52406527f;
        final StartGameMessage startGameMessage = new StartGameMessage();
        startGameMessage.setUserId(playerId);
        startGameMessage.setLongitude(playerLongitude);
        startGameMessage.setLatitude(playerLatitude);

        final Game game = new Game(111111222222L);
        when(gameService.startGame(eq(playerId), eq(playerLongitude), eq(playerLatitude))).thenReturn(game);
        when(gameAdapter.serialize(game)).thenReturn("gameId=111111222222");

        // when
        final String result = testee.handle(startGameMessage);

        // then
        verify(gameService).startGame(eq(playerId), eq(playerLongitude), eq(playerLatitude));
        assertEquals("TYPE:START_GAME_MESSAGE;status=200;gameId=111111222222", result);
    }
}
