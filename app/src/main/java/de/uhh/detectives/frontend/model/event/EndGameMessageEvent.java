package de.uhh.detectives.frontend.model.event;

import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.Message.EndGameMessage;
import de.uhh.detectives.frontend.model.event.api.MessageEvent;

public class EndGameMessageEvent implements MessageEvent {

    public EndGameMessage message;

    public EndGameMessageEvent() {}

    public EndGameMessageEvent(EndGameMessage message) {
        this.message = message;
    }

    @Override
    public EndGameMessage getMessage() {
        return message;
    }

    @Override
    public MessageEvent generateMessageEvent(String input) {
        return new EndGameMessageEvent(new EndGameMessage(input));
    }

    @Override
    public boolean accepts(MessageType messageType) {
        return messageType == MessageType.END_GAME_MESSAGE;
    }
}
