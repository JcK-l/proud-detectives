package de.uhh.detectives.backend.model.messaging;

import de.uhh.detectives.backend.service.api.messaging.MessageType;

public class EmptyMessage implements Message {

    @Override
    public MessageType getType() {
        return MessageType.UNKNOWN;
    }
}
