package de.uhh.detectives.frontend.model.event;

import de.uhh.detectives.frontend.model.Message.CluesGuessesStateMessage;
import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.Message.api.Message;
import de.uhh.detectives.frontend.model.event.api.MessageEvent;

public class CluesGuessesStateMessageEvent implements MessageEvent {

    public CluesGuessesStateMessage cluesGuessesStateMessage;

    public CluesGuessesStateMessageEvent() {}

    public CluesGuessesStateMessageEvent(CluesGuessesStateMessage cluesGuessesStateMessage) {
        this.cluesGuessesStateMessage = cluesGuessesStateMessage;
    }

    @Override
    public CluesGuessesStateMessage getMessage() {
        return cluesGuessesStateMessage;
    }

    @Override
    public MessageEvent generateMessageEvent(String input) {
        return new CluesGuessesStateMessageEvent(new CluesGuessesStateMessage(input));
    }

    @Override
    public boolean accepts(MessageType messageType) {
        return MessageType.CLUES_GUESSES_STATE_MESSAGE.equals(messageType);
    }
}
