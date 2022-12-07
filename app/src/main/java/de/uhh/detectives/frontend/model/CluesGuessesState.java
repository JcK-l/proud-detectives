package de.uhh.detectives.frontend.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

import de.uhh.detectives.frontend.ui.clues_and_guesses.Cell;

@Entity
public class CluesGuessesState {

    @PrimaryKey
    private Long playerId;

    private List<Cell> cells;
    private int suspicion_left;
    private String suspicion_left_tag;
    private int suspicion_middle;
    private String suspicion_middle_tag;
    private int suspicion_right;
    private String suspicion_right_tag;
    private int cardColor;
    private int numberOfTries;

    public CluesGuessesState() {
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public int getSuspicion_left() {
        return suspicion_left;
    }

    public void setSuspicion_left(int suspicion_left) {
        this.suspicion_left = suspicion_left;
    }

    public String getSuspicion_left_tag() {
        return suspicion_left_tag;
    }

    public void setSuspicion_left_tag(String suspicion_left_tag) {
        this.suspicion_left_tag = suspicion_left_tag;
    }

    public int getSuspicion_middle() {
        return suspicion_middle;
    }

    public void setSuspicion_middle(int suspicion_middle) {
        this.suspicion_middle = suspicion_middle;
    }

    public String getSuspicion_middle_tag() {
        return suspicion_middle_tag;
    }

    public void setSuspicion_middle_tag(String suspicion_middle_tag) {
        this.suspicion_middle_tag = suspicion_middle_tag;
    }

    public int getSuspicion_right() {
        return suspicion_right;
    }

    public void setSuspicion_right(int suspicion_right) {
        this.suspicion_right = suspicion_right;
    }

    public String getSuspicion_right_tag() {
        return suspicion_right_tag;
    }

    public void setSuspicion_right_tag(String suspicion_right_tag) {
        this.suspicion_right_tag = suspicion_right_tag;
    }

    public int getCardColor() {
        return cardColor;
    }

    public void setCardColor(int cardColor) {
        this.cardColor = cardColor;
    }

    public int getNumberOfTries() {
        return numberOfTries;
    }

    public void setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }
}