package de.uhh.detectives.backend.model.messaging;

import de.uhh.detectives.backend.service.api.messaging.MessageType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class WinGameMessage implements Serializable, Message {

    @Serial
    private static final long serialVersionUID = 3668179364988149695L;

    private Long senderId;

    @Override
    public MessageType getType() {
        return MessageType.WIN_GAME_MESSAGE;
    }
}
