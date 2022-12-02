package de.uhh.detectives.backend.model.messaging;

import de.uhh.detectives.backend.service.api.messaging.MessageType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class StartGameMessage implements Serializable, Message {

    @Serial
    private static final long serialVersionUID = 5602367142978805368L;

    private Long userId;
    private Float longitude;
    private Float latitude;

    @Override
    public MessageType getType() {
        return MessageType.START_GAME_MESSAGE;
    }
}
