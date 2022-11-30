package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.enumeration.Culprit;
import de.uhh.detectives.backend.model.enumeration.Location;
import de.uhh.detectives.backend.model.enumeration.Weapon;
import de.uhh.detectives.backend.repository.GameRepository;
import de.uhh.detectives.backend.repository.PlayerRepository;
import de.uhh.detectives.backend.service.api.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameServiceImpl.class);

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public GameServiceImpl(final GameRepository gameRepository, final PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public Long generateGame(final Long timestamp) {
        final Game game = new Game(timestamp);
        generateSolution(game);
        LOG.info(String.format("saving generated game with ID: %d", game.getGameId()));
        gameRepository.save(game);
        return game.getGameId();
    }

    @Override
    public boolean isJoinableGameAvailable() {
        final List<Game> nonStartedGames = gameRepository.findAllByStartedFalse();
        return !nonStartedGames.isEmpty();
    }

    @Override
    public void registerPlayer(final Long userId) {
        final List<Game> nonStartedGames = gameRepository.findAllByStartedFalse();
        if (nonStartedGames.size() != 1) {
            LOG.error(String.format("not exactly one active non-started game available for the user %d to join.", userId));
            return;
        }
        final Game nonStartedGame = nonStartedGames.get(0);
        final Optional<Player> player = playerRepository.findById(userId);
        if (player.isEmpty()) {
            LOG.error(String.format("No registered user with id %d found in the database.", userId));
            return;
        }
        nonStartedGame.getParticipants().add(player.get());
        gameRepository.save(nonStartedGame);
        LOG.info(String.format("registered user %d for game %d", userId, nonStartedGame.getGameId()));
    }

    @Override
    public void startGame(final Long userIdOfStartingUser) {
        // TODO implement
    }

    @Override
    public Long findActiveGameForUser(final Long userId) {
        // TODO implement
        return null;
    }

    @Override
    public List<Long> findUsersForGame(final Long gameId) {
        // TODO implement
        return null;
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
}
