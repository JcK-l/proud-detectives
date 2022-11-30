package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.repository.GameRepository;
import de.uhh.detectives.backend.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

    @InjectMocks
    private GameServiceImpl testee;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Test
    public void testGameGeneration() {
        // given
        final Long timestamp = 111111111L;
        when(gameRepository.save(any())).thenReturn(new Game(timestamp));
        final ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        final Long gameId = testee.generateGame(timestamp);

        // then
        assertEquals(timestamp, gameId);
        verify(gameRepository).save(captor.capture());
        final Game actual = captor.getValue();
        assertEquals(timestamp, actual.getGameId());
        assertNotNull(actual.getParticipants());
        assertEquals(0, actual.getParticipants().size());
        assertNotNull(actual.getCulprit());
        assertNotNull(actual.getLocation());
        assertNotNull(actual.getWeapon());
    }

    @Test
    public void testIsJoinableGameAvailable() {
        // given
        final List<Game> joinableGames = new ArrayList<>();
        joinableGames.add(new Game());
        when(gameRepository.findAllByStartedFalse()).thenReturn(joinableGames);

        // when
        final boolean result = testee.isJoinableGameAvailable();

        // then
        assertTrue(result);
    }

    @Test
    public void testRegisterPlayerDoesNotFindGame() {
        // given
        when(gameRepository.findAllByStartedFalse()).thenReturn(Collections.emptyList());

        // when
        testee.registerPlayer(123L);

        // then
        verify(playerRepository, times(0)).findById(anyLong());
        verify(gameRepository, times(0)).save(any());
    }

    @Test
    public void testRegisterPlayerDoesNotFindCorrespondingUser() {
        // given
        final List<Game> joinableGames = new ArrayList<>();
        joinableGames.add(new Game());
        when(gameRepository.findAllByStartedFalse()).thenReturn(joinableGames);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        testee.registerPlayer(123L);

        // then
        verify(playerRepository).findById(anyLong());
        verify(gameRepository, times(0)).save(any());
    }

    @Test
    public void testRegisterPlayer() {
        // given
        final List<Game> joinableGames = new ArrayList<>();
        joinableGames.add(new Game(11111111L));
        when(gameRepository.findAllByStartedFalse()).thenReturn(joinableGames);

        final Player player = new Player();
        player.setId(123L);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));

        final ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        testee.registerPlayer(123L);

        // then
        verify(playerRepository).findById(eq(123L));
        verify(gameRepository).save(captor.capture());
        final Game savedGame = captor.getValue();
        assertEquals(1, savedGame.getParticipants().size());
        assertEquals(123L, savedGame.getParticipants().get(0).getId());
    }
}
