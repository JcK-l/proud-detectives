package de.uhh.detectives.frontend.model.Message;

import de.uhh.detectives.frontend.model.Message.api.Message;

public class EndGameMessage implements Message {

    // for sending
    private Long senderId;
    private boolean win;

    // for receiving
    private int status;
    private Long gameId;
    private Long winnerId;
    private String winnerPseudonym;

    // Create WinGameMessage
    public EndGameMessage(Long senderId, boolean win) {
        this.senderId = senderId;
        this.win = win;
    }

    // Parse WinGameMessage
    public EndGameMessage(final String messageToParse) {
        String[] tokens = messageToParse.split(";");
        for (int i = 1; i < tokens.length; i++) {
            tokens[i] = tokens[i].substring(tokens[i].indexOf("=") + 1);
        }

        this.status = Integer.parseInt(tokens[1]);
        this.gameId = (tokens[2].equals("null")) ? null : Long.parseLong(tokens[2]);
        this.winnerId = (tokens[3].equals("null")) ? null : Long.parseLong(tokens[3]);
        this.winnerPseudonym = (tokens[4].equals("null")) ? null : tokens[4];
    }

    public boolean isWin() {
        return win;
    }

    public Long getSenderId() {
        return senderId;
    }

    public int getStatus() {
        return status;
    }

    public Long getGameId() {
        return gameId;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public String getWinnerPseudonym() {
        return winnerPseudonym;
    }

    @Override
    public String toString() {
        return "TYPE:END_GAME_MESSAGE" +
                ";senderId=" + this.senderId +
                ";win=" + this.win;
    }

    @Override
    public MessageType getType() {
        return MessageType.END_GAME_MESSAGE;
    }
}
