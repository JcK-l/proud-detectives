package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.ChatMessage;
import de.uhh.detectives.backend.model.EmptyMessage;
import de.uhh.detectives.backend.repository.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceImplTest {

    @InjectMocks
    private ChatMessageServiceImpl testee;

    @Mock
    private ChatMessageRepository chatMessageRepository;

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

        final ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);

        // when
        testee.handle(chatMessage);

        // then
        verify(chatMessageRepository).save(captor.capture());
        final ChatMessage actual = captor.getValue();
        assertEquals(1669494958668L, actual.getSenderId());
        assertEquals("tester", actual.getReceiverPseudonym());
        assertEquals("this is a test message", actual.getMessageContent());
        assertEquals(1669505325493L, actual.getTimestamp());
    }

}
