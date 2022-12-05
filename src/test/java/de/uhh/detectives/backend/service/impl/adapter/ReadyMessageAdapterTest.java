package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.ReadyMessage;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReadyMessageAdapterTest {

    private final ReadyMessageAdapter testee = new ReadyMessageAdapter();

    @Test
    public void testAccepts() {
        final Message readyMessage = new ReadyMessage();
        assertTrue(testee.accepts(readyMessage.getType()));
    }

    @Test
    public void testNotAccepts() {
        final MessageType type = MessageType.WIN_GAME_MESSAGE;
        assertFalse(testee.accepts(type));
    }

    @Test
    public void testConstructFromFields(){
        // given
        final String[] fields = new String[] {"TYPE:READY_MESSAGE", "senderId=123456789", "ready=false"};

        final ReadyMessage expected = new ReadyMessage();
        expected.setSenderId(123456789L);
        expected.setReady(false);

        // when
        final Message message = testee.constructFromFields(fields);

        // then
        assertTrue(message instanceof ReadyMessage);

        final ReadyMessage actual = (ReadyMessage) message;
        assertEquals(expected, actual);
    }

    @Test
    public void testConstructFromFieldsReadyTrue(){
        // given
        final String[] fields = new String[] {"TYPE:READY_MESSAGE", "senderId=123456789", "ready=true"};

        final ReadyMessage expected = new ReadyMessage();
        expected.setSenderId(123456789L);
        expected.setReady(true);

        // when
        final Message message = testee.constructFromFields(fields);

        // then
        assertTrue(message instanceof ReadyMessage);

        final ReadyMessage actual = (ReadyMessage) message;
        assertEquals(expected, actual);
    }
}
