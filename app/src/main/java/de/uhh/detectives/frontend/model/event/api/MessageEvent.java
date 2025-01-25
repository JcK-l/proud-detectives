package de.uhh.detectives.frontend.model.event.api;

import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.Message.api.Message;

public interface MessageEvent {
    Message getMessage();

    MessageEvent generateMessageEvent(String input);

    boolean accepts(MessageType messageType);
}
