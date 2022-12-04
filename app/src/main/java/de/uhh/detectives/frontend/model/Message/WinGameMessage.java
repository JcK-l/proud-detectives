package de.uhh.detectives.frontend.model.Message;

import de.uhh.detectives.frontend.model.Message.api.Message;

public class WinGameMessage implements Message {

    // for sending
    private Long senderId;

    // for receiving
    private int status;
    private Long gameId;
    private Long winnerId;
    private String winnerPseudonym;

    // Create WinGameMessage
    public WinGameMessage(Long senderId) {
        this.senderId = senderId;
    }

    // Parse WinGameMessage
    public WinGameMessage(final String messageToParse) {
        String[] tokens = messageToParse.split(";");
        for (int i = 1; i < tokens.length; i++) {
            tokens[i] = tokens[i].substring(tokens[i].indexOf("=") + 1);
        }

        this.status = Integer.parseInt(tokens[1]);
        this.gameId = Long.parseLong(tokens[2]);
        this.winnerId = Long.parseLong(tokens[3]);
        this.winnerPseudonym = tokens[4];
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public String getWinnerPseudonym() {
        return winnerPseudonym;
    }

    public void setWinnerPseudonym(String winnerPseudonym) {
        this.winnerPseudonym = winnerPseudonym;
    }

    @Override
    public String toString() {
        return "TYPE:WIN_GAME_MESSAGE" +
                ";senderId=" + this.senderId;
    }

    @Override
    public MessageType getType() {
        return MessageType.WIN_GAME_MESSAGE;
    }
}
