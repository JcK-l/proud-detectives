package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.CluesGuessesStateMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CluesGuessesStateMessageAdapterTest {

    private final CluesGuessesStateMessageAdapter testee = new CluesGuessesStateMessageAdapter();

    @Test
    public void testAccepts() {
        final Message cluesGuessesStateMessage = new CluesGuessesStateMessage();
        assertTrue(testee.accepts(cluesGuessesStateMessage.getType()));
    }

    @Test
    public void testNotAccepts() {
        final MessageType type = MessageType.JOIN_GAME_MESSAGE;
        assertFalse(testee.accepts(type));
    }

    @Test
    public void testConstructFromFields(){
        // given
        final String[] fields = new String[] {"TYPE:CLUES_GUESSES_STATE_MESSAGE", "playerId=1670522813436",
                "cardColor=2131100288", "numberOfTries=3", "suspicionLeft=2131230896", "suspicionMiddle=2131230894",
                "suspicionRight=2131230884", "cellJson=[{\"category\":\"waffe\",\"description\":\"pistole\",\"image\":2131230895,\"state\":\"NEUTRAL\"},{\"category\":\"waffe\",\"description\":\"dolch\",\"image\":2131230896,\"state\":\"NEUTRAL\"},{\"category\":\"waffe\",\"description\":\"seil\",\"image\":2131230897,\"state\":\"NEGATIVE\"},{\"category\":\"waffe\",\"description\":\"kerzenleuchter\",\"image\":2131230898,\"state\":\"NEUTRAL\"},{\"category\":\"waffe\",\"description\":\"rohrzange\",\"image\":2131230899,\"state\":\"NEUTRAL\"},{\"category\":\"waffe\",\"description\":\"heizungsrohr\",\"image\":2131230900,\"state\":\"NEUTRAL\"},{\"category\":\"waffe\",\"description\":\"messer\",\"image\":2131230901,\"state\":\"NEGATIVE\"},{\"category\":\"waffe\",\"description\":\"gift\",\"image\":2131230902,\"state\":\"NEGATIVE\"},{\"category\":\"waffe\",\"description\":\"hantel\",\"image\":2131230903,\"state\":\"NEGATIVE\"},{\"category\":\"person\",\"description\":\"dennis gatow\",\"image\":2131230889,\"state\":\"NEUTRAL\"},{\"category\":\"person\",\"description\":\"felix bloom\",\"image\":2131230890,\"state\":\"NEUTRAL\"},{\"category\":\"person\",\"description\":\"tom gruen\",\"image\":2131230891,\"state\":\"NEUTRAL\"},{\"category\":\"person\",\"description\":\"klara porz\",\"image\":2131230892,\"state\":\"NEGATIVE\"},{\"category\":\"person\",\"description\":\"gloria roth\",\"image\":2131230893,\"state\":\"NEUTRAL\"},{\"category\":\"person\",\"description\":\"diana weiss\",\"image\":2131230894,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"kueche\",\"image\":2131230879,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"musikzimmer\",\"image\":2131230881,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"schlafzimmer\",\"image\":2131230882,\"state\":\"NEGATIVE\"},{\"category\":\"ort\",\"description\":\"speisezimmer\",\"image\":2131230883,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"keller\",\"image\":2131230884,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"billardzimmer\",\"image\":2131230885,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"bibliothek\",\"image\":2131230886,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"garten\",\"image\":2131230887,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"eingangshalle\",\"image\":2131230888,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"arbeitszimmer\",\"image\":2131230880,\"state\":\"NEUTRAL\"}]"};

        final CluesGuessesStateMessage expected = new CluesGuessesStateMessage();

        expected.setCellString("[{\"category\":\"waffe\",\"description\":\"pistole\",\"image\":2131230895,\"state\":\"NEUTRAL\"},{\"category\":\"waffe\",\"description\":\"dolch\",\"image\":2131230896,\"state\":\"NEUTRAL\"},{\"category\":\"waffe\",\"description\":\"seil\",\"image\":2131230897,\"state\":\"NEGATIVE\"},{\"category\":\"waffe\",\"description\":\"kerzenleuchter\",\"image\":2131230898,\"state\":\"NEUTRAL\"},{\"category\":\"waffe\",\"description\":\"rohrzange\",\"image\":2131230899,\"state\":\"NEUTRAL\"},{\"category\":\"waffe\",\"description\":\"heizungsrohr\",\"image\":2131230900,\"state\":\"NEUTRAL\"},{\"category\":\"waffe\",\"description\":\"messer\",\"image\":2131230901,\"state\":\"NEGATIVE\"},{\"category\":\"waffe\",\"description\":\"gift\",\"image\":2131230902,\"state\":\"NEGATIVE\"},{\"category\":\"waffe\",\"description\":\"hantel\",\"image\":2131230903,\"state\":\"NEGATIVE\"},{\"category\":\"person\",\"description\":\"dennis gatow\",\"image\":2131230889,\"state\":\"NEUTRAL\"},{\"category\":\"person\",\"description\":\"felix bloom\",\"image\":2131230890,\"state\":\"NEUTRAL\"},{\"category\":\"person\",\"description\":\"tom gruen\",\"image\":2131230891,\"state\":\"NEUTRAL\"},{\"category\":\"person\",\"description\":\"klara porz\",\"image\":2131230892,\"state\":\"NEGATIVE\"},{\"category\":\"person\",\"description\":\"gloria roth\",\"image\":2131230893,\"state\":\"NEUTRAL\"},{\"category\":\"person\",\"description\":\"diana weiss\",\"image\":2131230894,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"kueche\",\"image\":2131230879,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"musikzimmer\",\"image\":2131230881,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"schlafzimmer\",\"image\":2131230882,\"state\":\"NEGATIVE\"},{\"category\":\"ort\",\"description\":\"speisezimmer\",\"image\":2131230883,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"keller\",\"image\":2131230884,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"billardzimmer\",\"image\":2131230885,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"bibliothek\",\"image\":2131230886,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"garten\",\"image\":2131230887,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"eingangshalle\",\"image\":2131230888,\"state\":\"NEUTRAL\"},{\"category\":\"ort\",\"description\":\"arbeitszimmer\",\"image\":2131230880,\"state\":\"NEUTRAL\"}]");
        expected.setCardColor(2131100288);
        expected.setNumberOfTries(3);
        expected.setSuspicionLeft(2131230896);
        expected.setSuspicionMiddle(2131230894);
        expected.setSuspicionRight(2131230884);
        expected.setPlayerId(1670522813436L);

        // when
        final Message message = testee.constructFromFields(fields);

        // then
        assertTrue(message instanceof CluesGuessesStateMessage);

        final CluesGuessesStateMessage actual = (CluesGuessesStateMessage) message;
        assertEquals(expected, actual);
    }
}
