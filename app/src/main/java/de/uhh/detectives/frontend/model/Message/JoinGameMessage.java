package de.uhh.detectives.frontend.model.Message;

import androidx.annotation.Nullable;

import de.uhh.detectives.frontend.model.Message.api.Message;
import de.uhh.detectives.frontend.model.UserData;

public class JoinGameMessage implements Message {
    // for sending
    @Nullable
    private Long userId;

    // for receiving
    private int status;
    @Nullable private Long gameId;

    public JoinGameMessage() {}

    // Create JoinGameMessage
    public JoinGameMessage(final UserData user) {
        this.userId = user.getUserId();

        this.status = 0;
        this.gameId = null;
    }

    // Parse JoinGameMessage
    public JoinGameMessage(String messageToParse) {
        String[] tokens = messageToParse.split(";");
        for (int i = 1; i < tokens.length; i++) {
            tokens[i] = tokens[i].substring(tokens[i].indexOf("=") + 1);
        }

        try {
            this.status = Integer.parseInt(tokens[1]);
            this.gameId = (tokens[2].equals("null")) ? null : Long.parseLong(tokens[2]);
        } catch (Exception e) {
            e.printStackTrace();
            this.status = 0;
            this.gameId = null;
        }
        this.userId = null;
    }

    @Nullable
    public Long getUserId() {
        return userId;
    }

    public int getStatus() {
        return status;
    }

    @Nullable
    public Long getGameId() {
        return gameId;
    }

    @Override
    public String toString() {
        return "TYPE:JOIN_GAME_MESSAGE" +
                ";userId=" + this.userId;
    }

    @Override
    public MessageType getType(){
        return MessageType.JOIN_GAME_MESSAGE;
    }
}
