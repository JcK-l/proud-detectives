package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.service.api.GameService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    @Override
    public Long findActiveGameForUser(Long userId) {
        // TODO implement
        return null;
    }

    @Override
    public List<Long> findUsersForGame(Long gameId) {
        // TODO implement
        return null;
    }
}
