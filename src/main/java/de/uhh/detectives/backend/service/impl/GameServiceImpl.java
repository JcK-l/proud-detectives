package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.Hint;
import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.enumeration.Culprit;
import de.uhh.detectives.backend.model.enumeration.Location;
import de.uhh.detectives.backend.model.enumeration.Weapon;
import de.uhh.detectives.backend.repository.GameRepository;
import de.uhh.detectives.backend.repository.PlayerRepository;
import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.LocationGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

    private static final int PLAYING_AREA_RADIUS_IN_METER = 2000;

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final LocationGenerator locationGenerator;

    public GameServiceImpl(final GameRepository gameRepository, final PlayerRepository playerRepository, final LocationGenerator locationGenerator) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.locationGenerator = locationGenerator;
    }

    @Override
    public void generateGame(final Long timestamp) {
        final Game game = new Game(timestamp);
        generateSolution(game);
        LOG.info(String.format("saving generated game with ID: %d", game.getGameId()));
        gameRepository.saveAndFlush(game);
    }

    @Override
    public boolean isJoinableGameAvailable() {
        final List<Game> nonStartedGames = gameRepository.findAllByStartedFalse();
        return !nonStartedGames.isEmpty();
    }

    @Override
    public Game registerPlayer(final Long userId) {
        final Game activeGameForPlayer = findActiveGameForUser(userId);
        if (activeGameForPlayer != null) {
            LOG.error(String.format("User %d already in a game. Will not register him/her for a new one.", userId));
            return null;
        }
        final List<Game> nonStartedGames = gameRepository.findAllByStartedFalse();
        if (nonStartedGames.size() != 1) {
            LOG.error(String.format("not exactly one active non-started game available for the user %d to join.", userId));
            return null;
        }
        final Game nonStartedGame = nonStartedGames.get(0);
        final Optional<Player> player = playerRepository.findById(userId);
        if (player.isEmpty()) {
            LOG.error(String.format("No registered user with id %d found in the database.", userId));
            return null;
        }
        nonStartedGame.getParticipants().add(player.get());
        gameRepository.save(nonStartedGame);
        LOG.info(String.format("registered user %d for game %d", userId, nonStartedGame.getGameId()));
        return nonStartedGame;
    }

    @Override
    public Game startGame(final Long userIdOfStartingUser, final Double longitudeOfUser, final Double latitudeOfUser,
                          final Integer playingFieldRadius) {
        final Game game = findActiveGameForUser(userIdOfStartingUser);
        if (game == null) {
            LOG.error(String.format("No game found for user %d, so none can be started!", userIdOfStartingUser));
            return null;
        }
        game.setHints(generateHints(game));
        giveOneHintToEachPlayer(game);
        doubleHintsNotInPlayerPossession(game);
        generateHintLocations(game, longitudeOfUser, latitudeOfUser, playingFieldRadius);
        game.setStarted(true);
        gameRepository.save(game);
        return game;
    }

    @Override
    public Game findActiveGameForUser(final Long userId) {
        final Optional<Player> playerOptional = playerRepository.findById(userId);
        if (playerOptional.isEmpty()) {
            LOG.error(String.format("No registered user with id %d found in the database.", userId));
            return null;
        }
        final List<Game> activeGames = gameRepository.findAllByCompletedFalse();
        final List<Game> gamesForUser = activeGames.stream()
                .filter(game -> game.getParticipants().contains(playerOptional.get()))
                .toList();
        if (gamesForUser.size() != 1) {
            LOG.warn(String.format("not exactly one active game was found for the user %d.", userId));
            return null;
        }
        return gamesForUser.get(0);
    }

    @Override
    public Game endGame(final Long winnerId) {
        final Game game = findActiveGameForUser(winnerId);
        game.setCompleted(true);
        game.setWinnerId(winnerId);
        LOG.info(String.format("Game %d ended and the winner was %d.", game.getGameId(), game.getWinnerId()));
        gameRepository.saveAndFlush(game);
        return game;
    }

    @Override
    public Game findLatestCompletedGameForUser(final Long userId) {
        final List<Game> completedGames = gameRepository.findAllByCompletedTrue();
        if (completedGames == null || completedGames.isEmpty()) {
            LOG.info("No completed game(s) found in the database.");
            return null;
        }
        final List<Game> gamesForUser = completedGames.stream()
                .filter(game -> hasUser(game, userId))
                .sorted(Comparator.comparingLong(Game::getGameId).reversed())
                .toList();
        if (gamesForUser.isEmpty()) {
            LOG.info(String.format("No completed game found for user %d", userId));
            return null;
        }
        return gamesForUser.get(0);
    }

    private void generateSolution(final Game game) {
        final Random random = ThreadLocalRandom.current();
        final int culpritRandomIndex = random.nextInt(0, Culprit.values().length);
        final int locationRandomIndex = random.nextInt(0, Location.values().length);
        final int weaponRandomIndex = random.nextInt(0, Weapon.values().length);

        final String culprit = Culprit.values()[culpritRandomIndex].getLabel();
        final String location = Location.values()[locationRandomIndex].getLabel();
        final String weapon = Weapon.values()[weaponRandomIndex].getLabel();

        game.setCulprit(culprit);
        game.setLocation(location);
        game.setWeapon(weapon);
    }

    private List<Hint> generateHints(final Game game) {
        final List<Hint> hints = new ArrayList<>();
        for (final Culprit culprit : Culprit.values()) {
            if (!culprit.getLabel().equals(game.getCulprit())) {
                hints.add(new Hint(culprit));
            }
        }
        for (final Location location : Location.values()) {
            if (!location.getLabel().equals(game.getLocation())) {
                hints.add(new Hint(location));
            }
        }
        for (final Weapon weapon : Weapon.values()) {
            if (!weapon.getLabel().equals(game.getWeapon())) {
                hints.add(new Hint(weapon));
            }
        }
        return hints;
    }

    private void giveOneHintToEachPlayer(final Game game) {
        final List<Integer> randomIndices = generateDistinctRandomIndices(game.getParticipants().size(), game.getHints().size());
        final List<Hint> hints = game.getHints();
        final List<Player> players = game.getParticipants();
        for (int i = 0; i < players.size(); i++) {
            final Hint hint = hints.get(randomIndices.get(i));
            hint.setPossessor(players.get(i));
        }
    }

    private List<Integer> generateDistinctRandomIndices(final int amountOfPlayers, final int maxIndex) {
        final Random random = ThreadLocalRandom.current();
        final List<Integer> randomIndices = new ArrayList<>();
        while (randomIndices.size() <= amountOfPlayers) {
            final int randomIndex = random.nextInt(0, maxIndex);
            if (!randomIndices.contains(randomIndex)) {
                randomIndices.add(randomIndex);
            }
        }
        return randomIndices;
    }

    private void doubleHintsNotInPlayerPossession(final Game game) {
        final List<Hint> allHints = new ArrayList<>();
        for (final Hint hint : game.getHints()) {
            if (hint.getPossessor() == null) {
                allHints.add(new Hint(hint.getItem()));
            }
            allHints.add(hint);
        }
        game.setHints(allHints);
    }

    private void generateHintLocations(final Game game, final Double longitudeOfUser, final Double latitudeOfUser,
                                       final Integer playingFieldRadius) {
        final Point center = new Point(longitudeOfUser, latitudeOfUser);
        final List<Hint> hintsWithoutPossessors = game.getHints().stream()
                .filter(hint -> hint.getPossessor() == null)
                .toList();
        final Random random = ThreadLocalRandom.current();
        final int radius = playingFieldRadius == null ? PLAYING_AREA_RADIUS_IN_METER : playingFieldRadius;
        final List<Point> randomLocations = locationGenerator.generateInCircle(center, radius,
                hintsWithoutPossessors.size(), random);

        for (int i = 0; i < hintsWithoutPossessors.size(); i++) {
            hintsWithoutPossessors.get(i).setLongitude(randomLocations.get(i).getX());
            hintsWithoutPossessors.get(i).setLatitude(randomLocations.get(i).getY());
        }
    }

    private boolean hasUser(final Game game, final Long userId) {
        final Optional<Player> player = game.getParticipants().stream().filter(p -> userId.equals(p.getId())).findAny();
        return player.isPresent();
    }
}
