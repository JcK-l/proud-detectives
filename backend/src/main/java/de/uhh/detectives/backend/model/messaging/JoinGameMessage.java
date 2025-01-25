package de.uhh.detectives.backend.model.messaging;

import de.uhh.detectives.backend.service.api.messaging.MessageType;
import lombok.Data;

@Data
public class JoinGameMessage implements Message {

    private Long senderId;

    @Override
    public MessageType getType() {
        return MessageType.JOIN_GAME_MESSAGE;
    }
}
