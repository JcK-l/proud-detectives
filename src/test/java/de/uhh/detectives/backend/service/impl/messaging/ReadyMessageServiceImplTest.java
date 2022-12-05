package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.ReadyMessage;
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
public class ReadyMessageServiceImplTest {

    @InjectMocks
    private ReadyMessageServiceImpl testee;

    @Mock
    private GameService gameService;

    @Test
    public void testAccepts() {
        final Message message = new ReadyMessage();
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
        final ReadyMessage readyMessage = new ReadyMessage();
        readyMessage.setSenderId(playerId);
        readyMessage.setReady(false);

        final Game game = new Game(111111111L);
        final Player player1 = new Player();
        player1.setId(playerId);
        player1.setPseudonym("Alice");
        final Participant participant1 = new Participant(player1);
        participant1.setReady(false);
        final Player player2 = new Player();
        player2.setId(playerId);
        player2.setPseudonym("Bob");
        final Participant participant2 = new Participant(player2);
        participant2.setReady(true);
        game.setParticipants(Arrays.asList(participant1, participant2));

        when(gameService.changeReadyStatus(eq(playerId), eq(false))).thenReturn(game);

        // when
        final String result = testee.handle(readyMessage);

        // then
        verify(gameService, times(0)).generateGame(anyLong());
        verify(gameService).changeReadyStatus(eq(playerId), eq(false));
        assertEquals("TYPE:READY_MESSAGE;status=200;gameId=111111111;players=Alice,Bob;playersReady=Bob", result);
    }

    @Test
    public void testHandleNoGameAvailable() {
        // given
        final Long playerId = 123456789L;
        final ReadyMessage readyMessage = new ReadyMessage();
        readyMessage.setSenderId(playerId);
        readyMessage.setReady(true);

        when(gameService.changeReadyStatus(eq(playerId), eq(true))).thenReturn(null);

        // when
        final String result = testee.handle(readyMessage);

        // then
        verify(gameService).changeReadyStatus(eq(playerId), eq(true));
        assertEquals("TYPE:READY_MESSAGE;status=418;gameId=null", result);
    }
}