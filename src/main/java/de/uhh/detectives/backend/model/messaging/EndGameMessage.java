package de.uhh.detectives.backend.model.messaging;

import de.uhh.detectives.backend.service.api.messaging.MessageType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class EndGameMessage implements Serializable, Message {

    @Serial
    private static final long serialVersionUID = 3668179364988149695L;

    private Long senderId;
    private boolean win;

    @Override
    public MessageType getType() {
        return MessageType.END_GAME_MESSAGE;
    }
}
