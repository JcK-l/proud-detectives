package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.messaging.EmptyMessage;
import de.uhh.detectives.backend.model.entity.ChatMessage;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.repository.ChatMessageRepository;
import de.uhh.detectives.backend.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
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

}
