package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.entity.ChatMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatMessageAdapterTest {

    private final ChatMessageAdapter testee = new ChatMessageAdapter();

    @Test
    public void testAccepts() {
        final MessageType type = MessageType.CHAT_MESSAGE;
        assertTrue(testee.accepts(type));
    }

    @Test
    public void testNotAccepts() {
        final MessageType type = MessageType.REGISTER_MESSAGE;
        assertFalse(testee.accepts(type));
    }

    @Test
    public void testConstructFromFields() {
        // given
        final String[] fields = new String[] {
                "TYPE:CHAT_MESSAGE",
                "senderId=1669494958668",
                "receiverId=tester",
                "message=this is a test message",
                "timestamp=1669505325493"
        };

        final ChatMessage expected = new ChatMessage();
        expected.setSenderId(1669494958668L);
        expected.setReceiverPseudonym("tester");
        expected.setMessageContent("this is a test message");
        expected.setTimestamp(1669505325493L);

        // when
        final Message message = testee.constructFromFields(fields);

        // then
        assertTrue(message instanceof ChatMessage);
        final ChatMessage actual = (ChatMessage) message;
        assertEquals(expected, actual);
    }

    @Test
    public void testToBroadcastString() {
        // given
        final String expected = "TYPE:CHAT_MESSAGE;senderId=1669494958668;receiverId=tester;message=this is a test message;timestamp=1669505325493;senderPseudonym=Mr. Tester";
        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(1669494958668L);
        chatMessage.setSenderPseudonym("Mr. Tester");
        chatMessage.setReceiverPseudonym("tester");
        chatMessage.setMessageContent("this is a test message");
        chatMessage.setTimestamp(1669505325493L);

        // when
        final String actual = testee.toBroadcastString(chatMessage);

        // then
        assertEquals(expected, actual);
    }
}
