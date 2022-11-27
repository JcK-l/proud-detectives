package de.uhh.detectives.backend.repository;

import de.uhh.detectives.backend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

}
