package de.uhh.detectives.frontend.model.event;

import de.uhh.detectives.frontend.model.Message.DirectMessage;
import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.event.api.MessageEvent;

public class DirectMessageEvent implements MessageEvent {
    private DirectMessage message;

    public DirectMessageEvent(DirectMessage message) {
        this.message = message;
    }

    @Override
    public DirectMessage getMessage() {
        return message;
    }

    // id;name
    @Override
    public MessageEvent generateMessageEvent(String input) {
        String[] tokens = input.split(";");
        return new DirectMessageEvent(new DirectMessage(tokens[0], tokens[1]));
    }

    @Override
    public boolean accepts(MessageType messageType) {
        return messageType == MessageType.UNKNOWN;
    }
}
