package de.uhh.detectives.backend.model.messaging;

import de.uhh.detectives.backend.service.api.messaging.MessageType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ReadyMessage implements Serializable, Message {

    @Serial
    private static final long serialVersionUID = 386978431793093933L;

    private Long senderId;
    private boolean ready;

    @Override
    public MessageType getType() {
        return MessageType.READY_MESSAGE;
    }
}
