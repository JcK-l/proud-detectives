package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.ChatMessage;
import de.uhh.detectives.backend.service.api.ChatMessageService;
import de.uhh.detectives.backend.service.api.MessageService;
import de.uhh.detectives.backend.service.api.MessageType;
import de.uhh.detectives.backend.service.api.RegisterMessageService;
import de.uhh.detectives.backend.service.api.adapter.MessageAdapter;
import de.uhh.detectives.backend.service.impl.adapter.ChatMessageAdapter;
import de.uhh.detectives.backend.service.impl.adapter.RegisterMessageAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TcpMessageServiceImplTest {

    @InjectMocks
    private TcpMessageServiceImpl testee;

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private RegisterMessageService registerMessageService;

    @Mock
    private List<MessageService> messageServices;

    @Mock
    private ChatMessageAdapter chatMessageAdapter;

    @Mock
    private RegisterMessageAdapter registerMessageAdapter;

    @Mock
    private List<MessageAdapter> messageAdapters;

    @Mock
    private Iterator<MessageService> messageServiceIterator;

    @Mock
    private Iterator<MessageAdapter> messageAdapterIterator;

    @BeforeEach
    public void setUp() {
        // set up services
        lenient().when(chatMessageService.accepts(eq(MessageType.CHAT_MESSAGE))).thenReturn(true);
        lenient().when(registerMessageService.accepts(eq(MessageType.REGISTER_MESSAGE))).thenReturn(true);
        when(messageServiceIterator.hasNext()).thenReturn(true, true, false);
        when(messageServiceIterator.next()).thenReturn(chatMessageService, registerMessageService);
        when(messageServices.iterator()).thenReturn(messageServiceIterator);

        // set up adapters
        lenient().when(chatMessageAdapter.accepts(eq(MessageType.CHAT_MESSAGE))).thenReturn(true);
        lenient().when(registerMessageAdapter.accepts(eq(MessageType.REGISTER_MESSAGE))).thenReturn(true);
        when(messageAdapterIterator.hasNext()).thenReturn(true, true, false);
        when(messageAdapterIterator.next()).thenReturn(chatMessageAdapter, registerMessageAdapter);
        when(messageAdapters.iterator()).thenReturn(messageAdapterIterator);
    }

    @Test
    public void testDeciphering() {
        // Given
        final String messageToBeDeciphered = "TYPE:CHAT_MESSAGE;senderId=1669494958668;receiverId=tester;message=this is a test message;timestamp=1669505325493;dateTime=12:28";
        final ChatMessage expected = new ChatMessage();
        expected.setSenderId(1669494958668L);
        expected.setReceiverPseudonym("tester");
        expected.setMessageContent("this is a test message");
        expected.setTimestamp(1669505325493L);

        when(chatMessageAdapter.constructFromFields(any())).thenReturn(expected);
        final ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);

        // when
        testee.receiveMessage(messageToBeDeciphered);

        // then
        assertEquals(expected, captor.getValue());
    }
}
