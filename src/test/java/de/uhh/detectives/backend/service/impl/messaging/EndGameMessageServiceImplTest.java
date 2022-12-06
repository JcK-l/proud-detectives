package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.messaging.JoinGameMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.EndGameMessage;
import de.uhh.detectives.backend.service.api.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EndGameMessageServiceImplTest {

    @InjectMocks
    private EndGameMessageServiceImpl testee;

    @Mock
    private GameService gameService;

    @Test
    public void testAccepts() {
        final Message message = new EndGameMessage();
        assertTrue(testee.accepts(message.getType()));
    }

    @Test
    public void testNotAccepts() {
        final Message message = new JoinGameMessage();
        assertFalse(testee.accepts(message.getType()));
    }

    @Test
    public void testHandleNoGameFound() {
        // given
        final Long playerId = 123456789L;
        final EndGameMessage endGameMessage = new EndGameMessage();
        endGameMessage.setSenderId(playerId);
        endGameMessage.setWin(true);
        when(gameService.endGame(eq(playerId), eq(true))).thenReturn(null);

        // when
        final String result = testee.handle(endGameMessage);

        // then
        verify(gameService).endGame(eq(playerId), eq(true));
        assertEquals("TYPE:END_GAME_MESSAGE;status=418;gameId=null", result);
    }

    @Test
    public void testHandleWin() {
        // given
        final Long playerId = 123456789L;
        final EndGameMessage endGameMessage = new EndGameMessage();
        endGameMessage.setSenderId(playerId);
        endGameMessage.setWin(true);

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
        game.setParticipants(Arrays.asList(new Participant(player1), new Participant(player2),
                new Participant(player3), new Participant(player4)));

        when(gameService.endGame(eq(playerId), eq(true))).thenReturn(game);

        // when
        final String result = testee.handle(endGameMessage);

        // then
        verify(gameService).endGame(eq(playerId), eq(true));
        assertEquals("TYPE:END_GAME_MESSAGE;status=200;gameId=11111111;winnerId=123456789;winnerPseudonym=Mr. Tester", result);
    }

    @Test
    public void testHandleLose() {
        // given
        final Long playerId = 123456789L;
        final EndGameMessage endGameMessage = new EndGameMessage();
        endGameMessage.setSenderId(playerId);
        endGameMessage.setWin(false);

        final Player player1 = new Player();
        player1.setId(1L);
        final Player player2 = new Player();
        player2.setId(2L);
        final Player player3 = new Player();
        player3.setId(playerId);
        player3.setPseudonym("Mr. Tester");
        final Participant participant = new Participant(player3);
        participant.setLost(true);
        final Player player4 = new Player();
        player4.setId(4L);

        final Long gameId = 11111111L;
        final Game game = new Game(gameId);
        game.setWinnerId(playerId);
        game.setParticipants(Arrays.asList(new Participant(player1), new Participant(player2), participant, new Participant(player4)));

        when(gameService.endGame(eq(playerId), eq(false))).thenReturn(game);

        // when
        final String result = testee.handle(endGameMessage);

        // then
        verify(gameService).endGame(eq(playerId), eq(false));
        assertNull(result);
    }

    @Test
    public void testHandleEveryoneLost() {
        // given
        final Long playerId = 123456789L;
        final EndGameMessage endGameMessage = new EndGameMessage();
        endGameMessage.setSenderId(playerId);
        endGameMessage.setWin(false);

        final Player player1 = new Player();
        player1.setId(1L);
        final Player player2 = new Player();
        player2.setId(2L);
        final Player player3 = new Player();
        player3.setId(playerId);
        player3.setPseudonym("Mr. Tester");
        final Player player4 = new Player();
        player4.setId(4L);

        final Participant participant1 = new Participant(player1);
        participant1.setLost(true);
        final Participant participant2 = new Participant(player2);
        participant2.setLost(true);
        final Participant participant3 = new Participant(player3);
        participant3.setLost(true);
        final Participant participant4 = new Participant(player4);
        participant4.setLost(true);

        final Long gameId = 11111111L;
        final Game game = new Game(gameId);
        game.setWinnerId(playerId);
        game.setParticipants(Arrays.asList(participant1, participant2, participant3, participant4));

        when(gameService.endGame(eq(playerId), eq(false))).thenReturn(game);

        // when
        final String result = testee.handle(endGameMessage);

        // then
        verify(gameService).endGame(eq(playerId), eq(false));
        assertEquals("TYPE:END_GAME_MESSAGE;status=200;gameId=11111111;winnerId=null;winnerPseudonym=null", result);
    }
}
