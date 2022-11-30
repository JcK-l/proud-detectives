package de.uhh.detectives.backend.service.api;

import java.util.List;

public interface GameService {

    Long findActiveGameForUser(final Long userId);

    boolean isJoinableGameAvailable();

    void registerPlayer(final Long userId);
    List<Long> findUsersForGame(final Long gameId);

    Long generateGame(final Long timestamp);

    void startGame(final Long userIdOfStartingUser);
}
