package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.CluesGuessesStateMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Component;

@Component
public class CluesGuessesStateMessageAdapter extends AbstractMessageAdapter{

    @Override
    public boolean accepts(MessageType messageType) {
        return MessageType.CLUES_GUESSES_STATE_MESSAGE.equals(messageType);
    }

    private static final int INDEX_PLAYER_ID = 1;
    private static final int INDEX_CARD_COLOR = 2;
    private static final int INDEX_NUMBER_OF_TRIES = 3;
    private static final int INDEX_SUSPICION_LEFT = 4;
    private static final int INDEX_SUSPICION_MIDDLE = 5;
    private static final int INDEX_SUSPICION_RIGHT = 6;
    private static final int INDEX_CELL_STRING = 7;

    @Override
    public Message constructFromFields(String[] fields) {
        getValues(fields);
        final CluesGuessesStateMessage message = new CluesGuessesStateMessage();
        message.setPlayerId(readLong(fields[INDEX_PLAYER_ID]));
        message.setCardColor(readInt(fields[INDEX_CARD_COLOR]));
        message.setNumberOfTries(readInt(fields[INDEX_NUMBER_OF_TRIES]));
        message.setSuspicionLeft(readInt(fields[INDEX_SUSPICION_LEFT]));
        message.setSuspicionMiddle(readInt(fields[INDEX_SUSPICION_MIDDLE]));
        message.setSuspicionRight(readInt(fields[INDEX_SUSPICION_RIGHT]));
        message.setCellString(fields[INDEX_CELL_STRING]);
        return message;
    }
}
