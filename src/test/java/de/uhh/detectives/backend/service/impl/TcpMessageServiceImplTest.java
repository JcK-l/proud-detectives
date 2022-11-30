package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.entity.ChatMessage;
import de.uhh.detectives.backend.service.api.messaging.MessageService;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import de.uhh.detectives.backend.service.api.adapter.MessageAdapter;
import de.uhh.detectives.backend.service.impl.adapter.ChatMessageAdapter;
import de.uhh.detectives.backend.service.impl.adapter.RegisterMessageAdapter;
import de.uhh.detectives.backend.service.impl.messaging.ChatMessageServiceImpl;
import de.uhh.detectives.backend.service.impl.messaging.RegisterMessageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TcpMessageServiceImplTest {

    @Test
    public void testDecipherChatMessage() {
        // Given
        final List<MessageService> messageServices = new ArrayList<>();
        final ChatMessageServiceImpl chatMessageService = mock(ChatMessageServiceImpl.class);
        when(chatMessageService.accepts(eq(MessageType.CHAT_MESSAGE))).thenReturn(true);
        messageServices.add(chatMessageService);
        final RegisterMessageServiceImpl registerMessageService = mock(RegisterMessageServiceImpl.class);
        messageServices.add(registerMessageService);

        final List<MessageAdapter> messageAdapters = new ArrayList<>();
        final ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter();
        messageAdapters.add(chatMessageAdapter);
        final RegisterMessageAdapter registerMessageAdapter = new RegisterMessageAdapter();
        messageAdapters.add(registerMessageAdapter);

        final TcpMessageServiceImpl testee = new TcpMessageServiceImpl(messageServices, messageAdapters);

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
        verify(chatMessageService).handle(captor.capture());
        verifyNoInteractions(registerMessageService);
        assertEquals(expected, captor.getValue());
    }

    @Test
    public void testDecipherMessageOfUnrecognizedType() {
        // Given
        final List<MessageService> messageServices = new ArrayList<>();
        final ChatMessageServiceImpl chatMessageService = mock(ChatMessageServiceImpl.class);
        messageServices.add(chatMessageService);
        final RegisterMessageServiceImpl registerMessageService = mock(RegisterMessageServiceImpl.class);
        messageServices.add(registerMessageService);

        final List<MessageAdapter> messageAdapters = new ArrayList<>();
        final ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter();
        messageAdapters.add(chatMessageAdapter);
        final RegisterMessageAdapter registerMessageAdapter = new RegisterMessageAdapter();
        messageAdapters.add(registerMessageAdapter);

        final TcpMessageServiceImpl testee = new TcpMessageServiceImpl(messageServices, messageAdapters);

        final String messageToBeDeciphered = "TYPE:VIRUS;virusId=54321;virusName=test virus";

        // when
        final String result = testee.receiveMessage(messageToBeDeciphered);

        // then
        verify(chatMessageService).accepts(eq(MessageType.UNKNOWN));
        verify(registerMessageService).accepts(eq(MessageType.UNKNOWN));
        assertNull(result);
    }
}
