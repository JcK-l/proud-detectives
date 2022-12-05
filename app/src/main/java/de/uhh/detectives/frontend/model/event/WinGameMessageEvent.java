package de.uhh.detectives.frontend.model.event;

import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.Message.WinGameMessage;
import de.uhh.detectives.frontend.model.event.api.MessageEvent;

public class WinGameMessageEvent implements MessageEvent {

    public WinGameMessage message;

    public WinGameMessageEvent() {}

    public WinGameMessageEvent(WinGameMessage message) {
        this.message = message;
    }

    @Override
    public WinGameMessage getMessage() {
        return message;
    }

    @Override
    public MessageEvent generateMessageEvent(String input) {
        return new WinGameMessageEvent(new WinGameMessage(input));
    }

    @Override
    public boolean accepts(MessageType messageType) {
        return messageType == MessageType.WIN_GAME_MESSAGE;
    }
}
