package de.uhh.detectives.backend.model;

import de.uhh.detectives.backend.service.api.MessageType;

public class EmptyMessage implements Message {

    @Override
    public MessageType getType() {
        return MessageType.UNKNOWN;
    }
}
