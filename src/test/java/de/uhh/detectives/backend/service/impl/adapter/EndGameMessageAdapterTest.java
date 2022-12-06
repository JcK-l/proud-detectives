package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.EndGameMessage;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EndGameMessageAdapterTest {

    private final EndGameMessageAdapter testee = new EndGameMessageAdapter();

    @Test
    public void testAccepts() {
        final Message winGameMessage = new EndGameMessage();
        assertTrue(testee.accepts(winGameMessage.getType()));
    }

    @Test
    public void testNotAccepts() {
        final MessageType type = MessageType.JOIN_GAME_MESSAGE;
        assertFalse(testee.accepts(type));
    }

    @Test
    public void testConstructFromFields(){
        // given
        final String[] fields = new String[] {"TYPE:WIN_GAME_MESSAGE", "senderId=123456789", "win=true"};

        final EndGameMessage expected = new EndGameMessage();
        expected.setSenderId(123456789L);
        expected.setWin(true);

        // when
        final Message message = testee.constructFromFields(fields);

        // then
        assertTrue(message instanceof EndGameMessage);

        final EndGameMessage actual = (EndGameMessage) message;
        assertEquals(expected, actual);
    }
}
