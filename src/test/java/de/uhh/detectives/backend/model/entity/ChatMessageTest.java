package de.uhh.detectives.backend.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChatMessageTest {

    @Test
    public void testGetAndSet() {
        // given
        final ChatMessage message = new ChatMessage();

        // when
        message.setMessageId(123L);
        message.setSenderId(345L);
        message.setSenderPseudonym("Mr. Tester");
        message.setReceiverPseudonym("Mr. Testee");
        message.setTimestamp(123456789L);
        message.setMessageContent("this is a simple test message content");

        // then
        assertNotNull(message);
        assertEquals(123L, message.getMessageId());
        assertEquals(345L, message.getSenderId());
        assertEquals("Mr. Tester", message.getSenderPseudonym());
        assertEquals("Mr. Testee", message.getReceiverPseudonym());
        assertEquals(123456789L, message.getTimestamp());
        assertEquals("this is a simple test message content", message.getMessageContent());
    }

    @Test
    public void testNotEquals() {
        // given
        final ChatMessage message1 = new ChatMessage();
        message1.setMessageId(123L);
        message1.setSenderId(345L);
        message1.setSenderPseudonym("Mr. Tester");
        message1.setReceiverPseudonym("Mr. Testee");
        message1.setTimestamp(123456789L);
        message1.setMessageContent("this is a simple test message content");

        final ChatMessage message2 = new ChatMessage();
        message2.setMessageId(124L);
        message2.setSenderId(345L);
        message2.setSenderPseudonym("Mr. Tester");
        message2.setReceiverPseudonym("Mr. Testee");
        message2.setTimestamp(123459789L);
        message2.setMessageContent("this is another simple test message content");

        // when
        final boolean result = message1.equals(message2);

        // then
        assertFalse(result);
    }

    @Test
    public void testEquals() {
        // given
        final ChatMessage message1 = new ChatMessage();
        message1.setMessageId(123L);
        message1.setSenderId(345L);
        message1.setSenderPseudonym("Mr. Tester");
        message1.setReceiverPseudonym("Mr. Testee");
        message1.setTimestamp(123456789L);
        message1.setMessageContent("this is a simple test message content");

        final ChatMessage message2 = new ChatMessage();
        message2.setMessageId(123L);
        message2.setSenderId(345L);
        message2.setSenderPseudonym("Mr. Tester");
        message2.setReceiverPseudonym("Mr. Testee");
        message2.setTimestamp(123456789L);
        message2.setMessageContent("this is a simple test message content");

        // when
        final boolean result = message1.equals(message2);

        // then
        assertTrue(result);
    }

    @Test
    public void testHashcodeNotEqual() {
        // given
        final ChatMessage message1 = new ChatMessage();
        message1.setMessageId(123L);
        message1.setSenderId(345L);
        message1.setSenderPseudonym("Mr. Tester");
        message1.setReceiverPseudonym("Mr. Testee");
        message1.setTimestamp(123456789L);
        message1.setMessageContent("this is a simple test message content");

        final ChatMessage message2 = new ChatMessage();
        message2.setMessageId(124L);
        message2.setSenderId(345L);
        message2.setSenderPseudonym("Mr. Tester");
        message2.setReceiverPseudonym("Mr. Testee");
        message2.setTimestamp(123459789L);
        message2.setMessageContent("this is another simple test message content");

        // when
        final int result1 = message1.hashCode();
        final int result2 = message2.hashCode();

        // then
        assertNotEquals(result1, result2);
    }

    @Test
    public void testHashcodeEqual() {
        // given
        final ChatMessage message1 = new ChatMessage();
        message1.setMessageId(123L);
        message1.setSenderId(345L);
        message1.setSenderPseudonym("Mr. Tester");
        message1.setReceiverPseudonym("Mr. Testee");
        message1.setTimestamp(123456789L);
        message1.setMessageContent("this is a simple test message content");

        final ChatMessage message2 = new ChatMessage();
        message2.setMessageId(123L);
        message2.setSenderId(345L);
        message2.setSenderPseudonym("Mr. Tester");
        message2.setReceiverPseudonym("Mr. Testee");
        message2.setTimestamp(123456789L);
        message2.setMessageContent("this is a simple test message content");

        // when
        final int result1 = message1.hashCode();
        final int result2 = message2.hashCode();

        // then
        assertEquals(result1, result2);
    }
}
