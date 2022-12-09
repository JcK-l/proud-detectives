package de.uhh.detectives.backend.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {

    @Test
    public void testGetAndSet() {
        // given
        final Player player = new Player();

        // when
        player.setId(123L);
        player.setPseudonym("Mr. Tester");
        player.setPrename("test");
        player.setSurname("tester");

        // then
        assertNotNull(player);
        assertEquals(123L, player.getId());
        assertEquals("Mr. Tester", player.getPseudonym());
        assertEquals("test", player.getPrename());
        assertEquals("tester", player.getSurname());
    }

    @Test
    public void testNotEquals() {
        // given
        final Player player1 = new Player();
        player1.setId(123L);
        player1.setPseudonym("Mr. Tester");
        player1.setPrename("test");
        player1.setSurname("tester");

        final Player player2 = new Player();
        player2.setId(124L);
        player2.setPseudonym("Max Mustermann");
        player2.setPrename("Max");
        player2.setSurname("Mustermann");

        // when
        final boolean result = player1.equals(player2);

        // then
        assertFalse(result);
    }

    @Test
    public void testEquals() {
        // given
        final Player player1 = new Player();
        player1.setId(123L);
        player1.setPseudonym("Mr. Tester");
        player1.setPrename("test");
        player1.setSurname("tester");

        final Player player2 = new Player();
        player2.setId(123L);
        player2.setPseudonym("Mr. Tester");
        player2.setPrename("test");
        player2.setSurname("tester");

        // when
        final boolean result = player1.equals(player2);

        // then
        assertTrue(result);
    }

    @Test
    public void testHashcodeNotEqual() {
        // given
        final Player player1 = new Player();
        player1.setId(123L);
        player1.setPseudonym("Mr. Tester");
        player1.setPrename("test");
        player1.setSurname("tester");

        final Player player2 = new Player();
        player2.setId(124L);
        player2.setPseudonym("Max Mustermann");
        player2.setPrename("Max");
        player2.setSurname("Mustermann");

        // when
        final int result1 = player1.hashCode();
        final int result2 = player2.hashCode();

        // then
        assertNotEquals(result1, result2);
    }

    @Test
    public void testHashcodeEqual() {
        // given
        final Player player1 = new Player();
        player1.setId(123L);
        player1.setPseudonym("Mr. Tester");
        player1.setPrename("test");
        player1.setSurname("tester");

        final Player player2 = new Player();
        player2.setId(123L);
        player2.setPseudonym("Mr. Tester");
        player2.setPrename("test");
        player2.setSurname("tester");

        // when
        final int result1 = player1.hashCode();
        final int result2 = player2.hashCode();

        // then
        assertEquals(result1, result2);
    }
}
