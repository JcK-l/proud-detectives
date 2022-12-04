package de.uhh.detectives.frontend.model.event;

import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.Message.RegisterMessage;
import de.uhh.detectives.frontend.model.event.api.MessageEvent;

public class RegisterMessageEvent implements MessageEvent {
    public RegisterMessage message;

    public RegisterMessageEvent() {}

    public RegisterMessageEvent(RegisterMessage message) {
        this.message = message;
    }

    public RegisterMessage getMessage() {
        return message;
    }

    @Override
    public MessageEvent generateMessageEvent(String input) {
       return new RegisterMessageEvent(new RegisterMessage(input));
    }

    @Override
    public boolean accepts(MessageType messageType) {
        return messageType == MessageType.REGISTER_MESSAGE;
    }
}
