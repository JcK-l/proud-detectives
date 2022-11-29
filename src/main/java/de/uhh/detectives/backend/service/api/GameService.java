package de.uhh.detectives.backend.service.api;

import java.util.List;

public interface GameService {

    Long findActiveGameForUser(final Long userId);
    List<Long> findUsersForGame(final Long gameId);
}
