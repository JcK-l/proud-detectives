package de.uhh.detectives.frontend.model.event;

import de.uhh.detectives.frontend.model.Message.JoinGameMessage;
import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.event.api.MessageEvent;

public class JoinGameMessageEvent implements MessageEvent {
    public JoinGameMessage message;

    public JoinGameMessageEvent() {
    }

    public JoinGameMessageEvent(JoinGameMessage message) {
        this.message = message;
    }

    public JoinGameMessage getMessage() {
        return message;
    }

    @Override
    public MessageEvent generateMessageEvent(String input) {
       return new JoinGameMessageEvent(new JoinGameMessage(input));
    }

    @Override
    public boolean accepts(MessageType messageType) {
        return messageType == MessageType.JOIN_GAME_MESSAGE;
    }
}
