package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.Hint;
import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.enumeration.Culprit;
import de.uhh.detectives.backend.model.enumeration.Location;
import de.uhh.detectives.backend.model.enumeration.Weapon;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameAdapterTest {

    private final GameAdapter testee = new GameAdapter();

    @Test
    public void testSerialize() {
        // given
        final Long gameId = 11111111L;
        final Game game = new Game(gameId);
        game.setWeapon("Pistole");
        game.setLocation("Arbeitszimmer");
        game.setCulprit("Tom Gruen");

        final Player player1 = new Player();
        player1.setId(123L);
        final Player player2 = new Player();
        player2.setId(234L);
        final Player player3 = new Player();
        player3.setId(345L);
        final Player player4 = new Player();
        player4.setId(456L);
        game.setParticipants(Arrays.asList(new Participant(player1), new Participant(player2),
                new Participant(player3), new Participant(player4)));

        final Hint hint1 = new Hint(Culprit.DENNIS_GATOW);
        hint1.setPossessor(player1);
        final Hint hint2 = new Hint(Location.SPEISEZIMMER);
        hint2.setLongitude(9.985102d);
        hint2.setLatitude(53.541350d);
        final Hint hint3 = new Hint(Culprit.KLARA_PORZ);
        hint3.setLongitude(9.994611d);
        hint3.setLatitude(53.540005d);
        final Hint hint4 = new Hint(Weapon.DOLCH);
        hint4.setPossessor(player2);
        final Hint hint5 = new Hint(Weapon.SEIL);
        hint5.setPossessor(player3);
        final Hint hint6 = new Hint(Location.EINGANGSHALLE);
        hint6.setPossessor(player4);
        final Hint hint7 = new Hint(Weapon.PISTOLE);
        hint7.setLongitude(9.996113d);
        hint7.setLatitude(53.540358d);
        final Hint hint8 = new Hint(Weapon.PISTOLE);
        hint7.setLongitude(9.993576d);
        hint7.setLatitude(53.539909d);
        game.setHints(Arrays.asList(hint1, hint2, hint3, hint4, hint5, hint6, hint7, hint8));


        // when
        final String actual = testee.serialize(game);

        // then
        assertEquals("gameId=11111111;culprit=Tom Gruen;location=Arbeitszimmer;weapon=Pistole;" +
                "Players[id=123;pseudonym=null;id=234;pseudonym=null;id=345;pseudonym=null;id=456;pseudonym=null;]" +
                "Hints[" +
                    "category=Person;description=Dennis Gatow;possessorId=123;longitude=null;latitude=null;" +
                    "category=Location;description=Speisezimmer;possessorId=null;longitude=9.985102;latitude=53.54135;" +
                    "category=Person;description=Klara Porz;possessorId=null;longitude=9.994611;latitude=53.540005;" +
                    "category=Weapon;description=Dolch;possessorId=234;longitude=null;latitude=null;" +
                    "category=Weapon;description=Seil;possessorId=345;longitude=null;latitude=null;" +
                    "category=Location;description=Eingangshalle;possessorId=456;longitude=null;latitude=null;" +
                    "category=Weapon;description=Pistole;possessorId=null;longitude=9.993576;latitude=53.539909;" +
                    "category=Weapon;description=Pistole;possessorId=null;longitude=null;latitude=null;" +
                "]", actual);
    }
}
