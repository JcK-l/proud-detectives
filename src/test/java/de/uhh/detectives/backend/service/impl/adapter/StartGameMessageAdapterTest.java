package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.JoinGameMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.StartGameMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartGameMessageAdapterTest {

    private final StartGameMessageAdapter testee = new StartGameMessageAdapter();

    @Test
    public void testAccepts() {
        final Message startGameMessage = new StartGameMessage();
        assertTrue(testee.accepts(startGameMessage.getType()));
    }

    @Test
    public void testNotAccepts() {
        final Message joinGameMessage = new JoinGameMessage();
        assertFalse(testee.accepts(joinGameMessage.getType()));
    }

    @Test
    public void testConstructFromFields(){
        // given
        final String[] fields = new String[] {
                "TYPE:START_GAME_MESSAGE",
                "userId=123456789",
                "longitude=-0.158670367",
                "latitude=51.52406527"
        };

        final StartGameMessage expected = new StartGameMessage();
        expected.setUserId(123456789L);
        expected.setLongitude(-0.158670367f);
        expected.setLatitude(51.52406527f);

        // when
        final Message message = testee.constructFromFields(fields);

        // then
        assertTrue(message instanceof StartGameMessage);

        final StartGameMessage actual = (StartGameMessage) message;
        assertEquals(expected, actual);
    }

}
