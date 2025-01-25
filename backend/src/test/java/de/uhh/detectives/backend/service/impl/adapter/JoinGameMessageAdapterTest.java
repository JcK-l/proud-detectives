package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.JoinGameMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JoinGameMessageAdapterTest {

    private final JoinGameMessageAdapter testee = new JoinGameMessageAdapter();

    @Test
    public void testAccepts() {
        final MessageType type = MessageType.JOIN_GAME_MESSAGE;
        assertTrue(testee.accepts(type));
    }

    @Test
    public void testNotAccepts() {
        final MessageType type = MessageType.REGISTER_MESSAGE;
        assertFalse(testee.accepts(type));
    }

    @Test
    public void testConstructFromFields(){
        // given
        final String[] fields = new String[] {"TYPE:JOIN_GAME_MESSAGE", "senderId=123456789"};

        final JoinGameMessage expected = new JoinGameMessage();
        expected.setSenderId(123456789L);

        // when
        final Message message = testee.constructFromFields(fields);

        // then
        assertTrue(message instanceof JoinGameMessage);

        final JoinGameMessage actual = (JoinGameMessage) message;
        assertEquals(expected, actual);
    }
}
