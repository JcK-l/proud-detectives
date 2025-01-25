package de.uhh.detectives.frontend.model.event;

import de.uhh.detectives.frontend.model.Message.EmptyMessage;
import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.Message.api.Message;
import de.uhh.detectives.frontend.model.event.api.MessageEvent;

public class BasicEvent implements MessageEvent {
    @Override
    public Message getMessage() {
        return new EmptyMessage();
    }

    @Override
    public MessageEvent generateMessageEvent(String input) {
        return new BasicEvent();
    }

    @Override
    public boolean accepts(MessageType messageType) {
        return MessageType.UNKNOWN.equals(messageType);
    }
}
