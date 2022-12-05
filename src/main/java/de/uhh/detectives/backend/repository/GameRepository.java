package de.uhh.detectives.backend.repository;

import de.uhh.detectives.backend.model.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findAllByCompletedTrue();
    List<Game> findAllByCompletedFalse();

    List<Game> findAllByStartedFalse();
}
