package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.messaging.CluesGuessesStateMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.service.api.messaging.MessageService;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Service;

@Service
public class CluesGuessesStateMessageServiceImpl implements MessageService {

    @Override
    public boolean accepts(MessageType messageType) {
        return MessageType.CLUES_GUESSES_STATE_MESSAGE.equals(messageType);
    }

    @Override
    public String handle(Message message) {
        CluesGuessesStateMessage cluesGuessesStateMessage = (CluesGuessesStateMessage) message;
        Long playerId = cluesGuessesStateMessage.getPlayerId();
        int cardColor = cluesGuessesStateMessage.getCardColor();
        int numberOfTries = cluesGuessesStateMessage.getNumberOfTries();
        int suspicionLeft = cluesGuessesStateMessage.getSuspicionLeft();
        int suspicionMiddle = cluesGuessesStateMessage.getSuspicionMiddle();
        int suspicionRight = cluesGuessesStateMessage.getSuspicionRight();
        String cellString = cluesGuessesStateMessage.getCellString();

        return "TYPE:" + MessageType.CLUES_GUESSES_STATE_MESSAGE +
                ";playerId=" + playerId + ";cardColor=" + cardColor +
                ";numberOfTries=" + numberOfTries + ";suspicionLeft=" + suspicionLeft +
                ";suspicionMiddle=" + suspicionMiddle + ";suspicionRight=" + suspicionRight +
                ";cellJson=" + cellString;
    }
}
