package de.uhh.detectives.frontend.model.Message;

import de.uhh.detectives.frontend.model.Message.api.Message;

public class EmptyMessage implements Message {
    @Override
    public MessageType getType() {
        return MessageType.UNKNOWN;
    }
}
