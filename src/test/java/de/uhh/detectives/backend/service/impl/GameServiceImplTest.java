package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.Hint;
import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.repository.GameRepository;
import de.uhh.detectives.backend.repository.ParticipantRepository;
import de.uhh.detectives.backend.repository.PlayerRepository;
import de.uhh.detectives.backend.service.api.LocationGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Mock
    private LocationGenerator locationGenerator;

    @Mock
    private ParticipantRepository participantRepository;
    
    private static final Long USER_ID = 1234567L;

    @Test
    public void testGameGeneration() {
        // given
        final Long timestamp = 111111111L;
        when(gameRepository.saveAndFlush(any())).thenReturn(new Game(timestamp));
        final ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        testee.generateGame(timestamp);

        // then
        verify(gameRepository).saveAndFlush(captor.capture());
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
    public void registerPlayerAlreadyInGame() {
        // given
        final Player player = new Player();
        player.setId(USER_ID);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));

        final List<Game> activeGames = new ArrayList<>();
        activeGames.add(new Game(11111111L));
        activeGames.get(0).getParticipants().add(new Participant(player));
        when(gameRepository.findAllByCompletedFalse()).thenReturn(activeGames);

        // when
        final Game game = testee.registerPlayer(USER_ID);

        // then
        verify(gameRepository, times(0)).findAllByStartedFalse();
        verify(gameRepository, times(0)).save(any());
        verify(gameRepository, times(0)).saveAndFlush(any());
        assertNull(game);

    }

    @Test
    public void testRegisterPlayerDoesNotFindGame() {
        // given
        final Player player = new Player();
        player.setId(USER_ID);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(gameRepository.findAllByStartedFalse()).thenReturn(Collections.emptyList());

        // when
        testee.registerPlayer(USER_ID);

        // then
        verify(playerRepository).findById(anyLong());
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
        testee.registerPlayer(USER_ID);

        // then
        verify(playerRepository, times(2)).findById(anyLong());
        verify(gameRepository, times(0)).save(any());
    }

    @Test
    public void testRegisterPlayer() {
        // given
        final List<Game> joinableGames = new ArrayList<>();
        joinableGames.add(new Game(11111111L));
        when(gameRepository.findAllByStartedFalse()).thenReturn(joinableGames);

        final Player player = new Player();
        player.setId(USER_ID);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(participantRepository.saveAndFlush(any())).thenReturn(new Participant(player));

        final ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        testee.registerPlayer(USER_ID);

        // then
        verify(playerRepository, times(2)).findById(eq(USER_ID));
        verify(participantRepository).saveAndFlush(any());
        verify(gameRepository).save(captor.capture());
        final Game savedGame = captor.getValue();
        assertEquals(1, savedGame.getParticipants().size());
        assertEquals(USER_ID, savedGame.getParticipants().get(0).getPlayer().getId());
    }

    @Test
    public void testFindActiveGameForUserNoUserWithId() {
        // given
        when(playerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        final Game game = testee.findActiveGameForUser(USER_ID);

        // then
        verify(playerRepository).findById(anyLong());
        verify(gameRepository, times(0)).findAllByCompletedFalse();
        assertNull(game);
    }

    @Test
    public void testFindActiveGameForUserNoGameAvailable() {
        // given
        final Player player = new Player();
        player.setId(USER_ID);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(gameRepository.findAllByCompletedFalse()).thenReturn(Collections.emptyList());

        // when
        final Game game = testee.findActiveGameForUser(USER_ID);

        // then
        verify(playerRepository).findById(anyLong());
        verify(gameRepository).findAllByCompletedFalse();
        assertNull(game);
    }

    @Test
    public void testFindActiveGameForUser() {
        // given
        final Player player = new Player();
        player.setId(USER_ID);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));

        final List<Game> activeGames = new ArrayList<>();
        activeGames.add(new Game(11111111L));
        activeGames.get(0).getParticipants().add(new Participant(player));
        when(gameRepository.findAllByCompletedFalse()).thenReturn(activeGames);

        // when
        final Game game = testee.findActiveGameForUser(USER_ID);

        // then
        verify(playerRepository).findById(anyLong());
        verify(gameRepository).findAllByCompletedFalse();
        assertEquals(11111111L, game.getGameId());
    }

    @Test
    public void testStartGame() {
        // given
        final Player player1 = new Player();
        player1.setId(USER_ID);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player1));
        final Player player2 = new Player();
        player2.setId(2L);
        final Player player3 = new Player();
        player3.setId(3L);
        final Player player4 = new Player();
        player4.setId(4L);

        final Long gameId = 11111111L;
        final Game game = new Game(gameId);
        game.setWeapon("Pistole");
        game.setLocation("Arbeitszimmer");
        game.setCulprit("Tom Gruen");
        game.setParticipants(Arrays.asList(new Participant(player1), new Participant(player2),
                new Participant(player3), new Participant(player4)));
        when(gameRepository.findAllByCompletedFalse()).thenReturn(Collections.singletonList(game));

        final Point location1 = new Point(9.994611d, 53.540005d);
        final Point location2 = new Point(9.985102d, 53.541350d);
        final Integer defaultPlayingAreaSize = 2000;
        final Integer amount = 36;
        when(locationGenerator.generateInCircle(any(), eq(defaultPlayingAreaSize), eq(amount), any()))
                .thenReturn(Arrays.asList(
                        location1, location2, location1, location2, location1, location2, location1, location2, location1, location2,
                        location1, location2, location1, location2, location1, location2, location1, location2, location1, location2,
                        location1, location2, location1, location2, location1, location2, location1, location2, location1, location2,
                        location1, location2, location1, location2, location1, location2));

        // when
        final Game result = testee.startGame(USER_ID, 9.993682d, 53.551086d, null);

        // then
        verify(playerRepository).findById(anyLong());
        verify(gameRepository).findAllByCompletedFalse();
        verify(gameRepository).save(any());
        final List<Hint> hintsInPlayerPossession = result.getHints().stream()
                .filter(hint -> hint.getPossessor() != null).toList();
        assertEquals(4, hintsInPlayerPossession.size());
        final List<Hint> hintsWithoutLocation = result.getHints().stream()
                .filter(hint -> hint.getLatitude() == null && hint.getLongitude() == null).toList();
        assertEquals(4, hintsWithoutLocation.size());
        final List<Hint> hintsInRandomLocations = result.getHints().stream()
                .filter(hint -> hint.getLatitude() != null && hint.getLongitude() != null).toList();
        assertEquals(36, hintsInRandomLocations.size());
        final Set<Double> longitudes = hintsInRandomLocations.stream().map(Hint::getLongitude).collect(Collectors.toSet());
        assertEquals(2, longitudes.size());
        assertTrue(longitudes.contains(9.994611d));
        assertTrue(longitudes.contains(9.985102d));
        final Set<Double> latitudes = hintsInRandomLocations.stream().map(Hint::getLatitude).collect(Collectors.toSet());
        assertEquals(2, latitudes.size());
        assertTrue(latitudes.contains(53.540005d));
        assertTrue(latitudes.contains(53.541350d));
    }

    @Test
    public void testStartGamePlayerHasNoGame() {
        // given
        final Player player1 = new Player();
        player1.setId(1L);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player1));
        final Player player2 = new Player();
        player2.setId(2L);
        final Player player3 = new Player();
        player3.setId(3L);
        final Player player4 = new Player();
        player4.setId(4L);

        final Long gameId = 11111111L;
        final Game game = new Game(gameId);
        game.setWeapon("Pistole");
        game.setLocation("Arbeitszimmer");
        game.setCulprit("Tom Gruen");
        game.setParticipants(Arrays.asList(new Participant(player2), new Participant(player3), new Participant(player4)));
        when(gameRepository.findAllByCompletedFalse()).thenReturn(Collections.singletonList(game));

        // when
        final Game result = testee.startGame(1L, 9.993682d, 53.551086d, null);

        // then
        verify(playerRepository).findById(anyLong());
        verify(gameRepository).findAllByCompletedFalse();
        verify(gameRepository, times(0)).save(any());
        assertNull(result);
    }

    @Test
    public void testEndGame() {
        // given
        final Player player1 = new Player();
        player1.setId(1L);
        final Player player2 = new Player();
        player2.setId(2L);
        final Player player3 = new Player();
        player3.setId(USER_ID);
        final Player player4 = new Player();
        player4.setId(4L);

        final Long gameId = 11111111L;
        final Game game = new Game(gameId);
        game.setStarted(true);
        game.setParticipants(Arrays.asList(new Participant(player1), new Participant(player2),
                new Participant(player3), new Participant(player4)));
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player3));
        when(gameRepository.findAllByCompletedFalse()).thenReturn(Collections.singletonList(game));

        final ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);

        // when
        testee.endGame(USER_ID);

        // then
        verify(gameRepository).saveAndFlush(captor.capture());
        final Game actual = captor.getValue();
        assertNotNull(actual);
        assertEquals(4, actual.getParticipants().size());
        assertTrue(actual.isStarted());
        assertTrue(actual.isCompleted());
        assertEquals(USER_ID, actual.getWinnerId());
    }

    @Test
    public void testFindLatestButNoGamesCompleted() {
        // given
        when(gameRepository.findAllByCompletedTrue()).thenReturn(Collections.emptyList());

        // when
        final Game actual = testee.findLatestCompletedGameForUser(USER_ID);

        // then
        assertNull(actual);
    }

    @Test
    public void testFindLatestButNoCompletedGameHasUser() {
        // given
        final Game game1 = new Game(123L);
        final Game game2 = new Game(234L);
        when(gameRepository.findAllByCompletedTrue()).thenReturn(Arrays.asList(game1, game2));

        // when
        final Game actual = testee.findLatestCompletedGameForUser(USER_ID);

        // then
        assertNull(actual);
    }

    @Test
    public void testFindLatestCompletedGameForUser() {
        // given
        final Player player1 = new Player();
        player1.setId(1L);
        final Player player2 = new Player();
        player2.setId(2L);
        final Player player3 = new Player();
        player3.setId(USER_ID);
        final Player player4 = new Player();
        player4.setId(4L);

        final Game game1 = new Game(123L);
        game1.setCompleted(true);
        game1.setParticipants(Arrays.asList(new Participant(player1), new Participant(player2),
                new Participant(player3), new Participant(player4)));
        final Game game2 = new Game(345L);
        game2.setCompleted(true);
        game2.setParticipants(Arrays.asList(new Participant(player1), new Participant(player2),
                new Participant(player3), new Participant(player4)));
        final Game game3 = new Game(234L);
        game3.setCompleted(true);
        game3.setParticipants(Arrays.asList(new Participant(player1), new Participant(player2),
                new Participant(player3), new Participant(player4)));
        final Game game4 = new Game(777L);
        game4.setCompleted(true);
        game4.setParticipants(Arrays.asList(new Participant(player1), new Participant(player2),
                new Participant(player4)));

        when(gameRepository.findAllByCompletedTrue()).thenReturn(Arrays.asList(game1, game2, game3));

        // when
        final Game actual = testee.findLatestCompletedGameForUser(USER_ID);

        // then
        assertNotNull(actual);
        assertTrue(actual.isCompleted());
        assertEquals(345L, actual.getGameId());
    }

    @Test
    public void testChangeReadyStatusNoActiveGame() {
        // given
        final Player player = new Player();
        player.setId(USER_ID);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        when(gameRepository.findAllByCompletedFalse()).thenReturn(Collections.emptyList());

        // when
        final Game game = testee.changeReadyStatus(USER_ID, true);

        // then
        verify(playerRepository).findById(eq(USER_ID));
        verify(participantRepository, times(0)).saveAndFlush(any());
        assertNull(game);
    }

    @Test
    public void testChangeReadyStatus() {
        // given
        final Player player = new Player();
        player.setId(USER_ID);
        when(playerRepository.findById(anyLong())).thenReturn(Optional.of(player));
        final Participant participant = new Participant(player);
        participant.setReady(false);

        final Game expected = new Game(111111111L);
        final Player player1 = new Player();
        player1.setId(USER_ID);
        player1.setPseudonym("Alice");
        final Participant participant1 = new Participant(player1);
        participant1.setReady(false);
        final Player player2 = new Player();
        player2.setId(USER_ID);
        player2.setPseudonym("Bob");
        final Participant participant2 = new Participant(player2);
        participant2.setReady(true);
        expected.setParticipants(Arrays.asList(participant, participant1, participant2));
        when(gameRepository.findAllByCompletedFalse()).thenReturn(Collections.singletonList(expected));

        final ArgumentCaptor<Participant> captor = ArgumentCaptor.forClass(Participant.class);

        // when
        final Game game = testee.changeReadyStatus(USER_ID, true);

        // then
        verify(playerRepository).findById(eq(USER_ID));
        verify(participantRepository).saveAndFlush(captor.capture());
        final Participant actual = captor.getValue();
        assertEquals(participant.getPlayer(), actual.getPlayer());
        assertTrue(actual.isReady());

        assertNotNull(game);
        assertEquals(3, game.getParticipants().size());
        assertTrue(game.getParticipants().get(0).isReady());
        assertFalse(game.getParticipants().get(1).isReady());
        assertTrue(game.getParticipants().get(2).isReady());
    }
}
