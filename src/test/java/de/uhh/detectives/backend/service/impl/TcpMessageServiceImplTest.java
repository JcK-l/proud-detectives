package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.ChatMessage;
import de.uhh.detectives.backend.service.api.ChatMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TcpMessageServiceImplTest {

    @InjectMocks
    private TcpMessageServiceImpl testee;

    @Mock
    private ChatMessageService chatMessageService;

    @Test
    public void testDeciphering() {
        // Given
        final String messageToBeDeciphered = "TYPE:CHAT_MESSAGE;senderId=1669494958668;receiverId=tester;message=this is a test message;timestamp=1669505325493;dateTime=12:28";
        final ChatMessage expected = new ChatMessage();
        expected.setSenderId(1669494958668L);
        expected.setReceiverPseudonym("tester");
        expected.setMessageContent("this is a test message");
        expected.setTimestamp(1669505325493L);

        final ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);

        // when
        testee.receiveMessage(messageToBeDeciphered);

        // then
        verify(chatMessageService).saveMessage(captor.capture());
        assertEquals(expected, captor.getValue());
    }
}
