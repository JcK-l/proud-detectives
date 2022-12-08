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
    private int suspicionLeft;
    private String suspicionLeftTag;
    private int suspicionMiddle;
    private String suspicionMiddleTag;
    private int suspicionRight;
    private String suspicionRightTag;
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

    public int getSuspicionLeft() {
        return suspicionLeft;
    }

    public void setSuspicionLeft(int suspicionLeft) {
        this.suspicionLeft = suspicionLeft;
    }

    public String getSuspicionLeftTag() {
        return suspicionLeftTag;
    }

    public void setSuspicionLeftTag(String suspicionLeftTag) {
        this.suspicionLeftTag = suspicionLeftTag;
    }

    public int getSuspicionMiddle() {
        return suspicionMiddle;
    }

    public void setSuspicionMiddle(int suspicionMiddle) {
        this.suspicionMiddle = suspicionMiddle;
    }

    public String getSuspicionMiddleTag() {
        return suspicionMiddleTag;
    }

    public void setSuspicionMiddleTag(String suspicionMiddleTag) {
        this.suspicionMiddleTag = suspicionMiddleTag;
    }

    public int getSuspicionRight() {
        return suspicionRight;
    }

    public void setSuspicionRight(int suspicionRight) {
        this.suspicionRight = suspicionRight;
    }

    public String getSuspicionRightTag() {
        return suspicionRightTag;
    }

    public void setSuspicionRightTag(String suspicionRightTag) {
        this.suspicionRightTag = suspicionRightTag;
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