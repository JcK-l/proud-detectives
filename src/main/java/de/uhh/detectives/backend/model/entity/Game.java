package de.uhh.detectives.backend.model.entity;

import de.uhh.detectives.backend.model.Hint;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
public class Game implements Serializable {

    @Serial
    private static final long serialVersionUID = 2040661693437304598L;

    private Long gameId;
    private boolean completed;
    private Long winnerId;
    private boolean started;

    private String culprit;
    private String location;
    private String weapon;

    private Double centerX;
    private Double centerY;
    private Integer radius;

    private List<Participant> participants;
    private List<Hint> hints;

    public Game(final Long timestamp) {
        this.gameId = timestamp;
        this.participants = new ArrayList<>();
        this.hints = new ArrayList<>();
    }

    @Id
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    @NotNull
    public String getCulprit() {
        return culprit;
    }

    public void setCulprit(String culprit) {
        this.culprit = culprit;
    }

    @NotNull
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @NotNull
    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    public Double getCenterX() {
        return centerX;
    }

    public void setCenterX(Double centerX) {
        this.centerX = centerX;
    }

    public Double getCenterY() {
        return centerY;
    }

    public void setCenterY(Double centerY) {
        this.centerY = centerY;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    @OneToMany(fetch = FetchType.EAGER)
    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    @Transient
    public Participant getParticipant(final Long userId) {
        return participants.stream()
                .filter(participant -> userId.equals(participant.getPlayer().getId()))
                .findFirst().orElse(null);
    }

    @Transient
    public List<Hint> getHints() {
        return hints;
    }

    public void setHints(List<Hint> hints) {
        this.hints = hints;
    }
}
