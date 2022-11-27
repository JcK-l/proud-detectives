package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.Message;
import de.uhh.detectives.backend.model.Player;
import de.uhh.detectives.backend.model.RegisterMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterMessageAdapterTest {
    private final RegisterMessageAdapter testee = new RegisterMessageAdapter();

    @Test
    public void testConstructFromFields(){
        // given
        final String[] fields = new String[] {
                "TYPE:CHAT_MESSAGE",
                "userId=123456789",
                "pseudonym=testPseudonym",
                "prename=testPrename",
                "surname=testSurname"
        };

        final RegisterMessage expected = new RegisterMessage();
        expected.setUserId(123456789L);
        expected.setPseudonym("testPseudonym");
        expected.setSurname("testSurname");
        expected.setPrename("testPrename");

        // when
        final Message message = testee.constructFromFields(fields);

        // then
        assertTrue(message instanceof RegisterMessage);

        final RegisterMessage actual = (RegisterMessage) message;
        assertEquals(expected, actual);
    }

    @Test
    public void testCreatePlayerFromMessage(){
        // given
        final RegisterMessage expected = new RegisterMessage();
        expected.setUserId(123456789L);
        expected.setPseudonym("testPseudonym");
        expected.setSurname("testSurname");
        expected.setPrename("testPrename");

        // when
        final Player player = testee.createPlayerFromMessage(expected);

        // then
        assertEquals("testSurname", player.getSurname());
        assertEquals("testPrename", player.getPrename());
        assertEquals(123456789, player.getId());
        assertEquals("testPseudonym", player.getPseudonym());
    }

}
