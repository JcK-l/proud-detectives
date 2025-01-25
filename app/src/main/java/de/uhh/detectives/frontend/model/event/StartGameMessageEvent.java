package de.uhh.detectives.frontend.model.event;

import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.Message.StartGameMessage;
import de.uhh.detectives.frontend.model.event.api.MessageEvent;

public class StartGameMessageEvent implements MessageEvent {

    public StartGameMessage message;

    public StartGameMessageEvent() {}

    public StartGameMessageEvent(StartGameMessage message) {
        this.message = message;
    }

    @Override
    public StartGameMessage getMessage() {
        return message;
    }

    @Override
    public MessageEvent generateMessageEvent(String input) {
        return new StartGameMessageEvent(new StartGameMessage(input));
    }

    @Override
    public boolean accepts(MessageType messageType) {
        return messageType == MessageType.START_GAME_MESSAGE;
    }
}
