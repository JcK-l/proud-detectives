package de.uhh.detectives.backend.repository;

import de.uhh.detectives.backend.model.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
