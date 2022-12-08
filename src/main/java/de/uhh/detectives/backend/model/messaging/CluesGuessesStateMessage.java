package de.uhh.detectives.backend.model.messaging;

import de.uhh.detectives.backend.service.api.messaging.MessageType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CluesGuessesStateMessage implements Serializable, Message {

    @Serial
    private static final long serialVersionUID = 8884380163085391311L;

    private Long playerId;

    private String cellString;
    private int suspicionLeft;
    private int suspicionMiddle;
    private int suspicionRight;
    private int cardColor;
    private int numberOfTries;

    @Override
    public MessageType getType() {
        return MessageType.CLUES_GUESSES_STATE_MESSAGE;
    }
}
