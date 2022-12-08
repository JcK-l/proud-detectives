package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.messaging.EmptyMessage;
import de.uhh.detectives.backend.model.entity.ChatMessage;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.repository.ChatMessageRepository;
import de.uhh.detectives.backend.repository.PlayerRepository;
import de.uhh.detectives.backend.service.api.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceImplTest {

    @InjectMocks
    private ChatMessageServiceImpl testee;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameService gameService;

    @Test
    public void testAccepts() {
        final ChatMessage chatMessage = new ChatMessage();
        assertTrue(testee.accepts(chatMessage.getType()));
    }

    @Test
    public void testNotAccepts() {
        final EmptyMessage emptyMessage = new EmptyMessage();
        assertFalse(testee.accepts(emptyMessage.getType()));
    }

    @Test
    public void testHandle(){
        // given
        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(1669494958668L);
        chatMessage.setReceiverPseudonym("tester");
        chatMessage.setMessageContent("this is a test message");
        chatMessage.setTimestamp(1669505325493L);

        final Player sender = new Player();
        sender.setPseudonym("Mr. Tester");
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(sender));

        final ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);

        final Game game = new Game(111111111L);

        when(gameService.findActiveGameForUser(eq(1669494958668L))).thenReturn(game);

        // when
        testee.handle(chatMessage);

        // then
        verify(chatMessageRepository).save(captor.capture());
        final ChatMessage actual = captor.getValue();
        assertEquals(1669494958668L, actual.getSenderId());
        assertEquals("Mr. Tester", actual.getSenderPseudonym());
        assertEquals("tester", actual.getReceiverPseudonym());
        assertEquals("this is a test message", actual.getMessageContent());
        assertEquals(1669505325493L, actual.getTimestamp());
    }
    @Test
    public void testHandleDead(){
        // given
        final ChatMessage chatMessage = new ChatMessage();
        final Long playerId = 123456789L;
        chatMessage.setSenderId(playerId);

        final Game game = new Game(111111111L);
        final Player player1 = new Player();
        player1.setId(playerId);
        player1.setPseudonym("Mr. Tester");
        final Participant participant1 = new Participant(player1);
        participant1.setLost(true);
        final Player player2 = new Player();
        player2.setId(1L);
        final Participant participant2 = new Participant(player2);
        game.setParticipants(Arrays.asList(participant1, participant2));

        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player1));
        when(gameService.findActiveGameForUser(eq(playerId))).thenReturn(game);

        // when
        String result = testee.handle(chatMessage);
        String[] resultTokens = result.split(";");

        // then
        assertEquals("dead=true", resultTokens[resultTokens.length - 1]);
    }

    @Test
    public void testHandleAlive(){
        // given
        final ChatMessage chatMessage = new ChatMessage();
        final Long playerId = 123456789L;
        chatMessage.setSenderId(playerId);

        final Game game = new Game(111111111L);
        final Player player1 = new Player();
        player1.setId(playerId);
        player1.setPseudonym("Mr. Tester");
        final Participant participant1 = new Participant(player1);
        participant1.setLost(false);
        final Player player2 = new Player();
        player2.setId(1L);
        final Participant participant2 = new Participant(player2);
        game.setParticipants(Arrays.asList(participant1, participant2));

        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player1));
        when(gameService.findActiveGameForUser(eq(playerId))).thenReturn(game);

        // when
        String result = testee.handle(chatMessage);
        String[] resultTokens = result.split(";");

        // then
        assertEquals("dead=false", resultTokens[resultTokens.length - 1]);
    }
}
