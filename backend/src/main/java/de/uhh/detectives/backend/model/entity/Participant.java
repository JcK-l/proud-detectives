package de.uhh.detectives.backend.model.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serial;
import java.io.Serializable;

@Entity
public class Participant implements Serializable {

    @Serial
    private static final long serialVersionUID = -6411871418843295013L;

    private Long participantId;
    private Player player;
    private boolean ready;
    private boolean lost;

    public Participant() {}

    public Participant(final Player player) {
        this.player = player;
        this.ready = false;
    }

    @Id
    @GeneratedValue
    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }
}
