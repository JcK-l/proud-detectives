package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.EndGameMessage;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Component;

@Component
public class EndGameMessageAdapter extends AbstractMessageAdapter {

    private static final int INDEX_USER_ID = 1;
    private static final int INDEX_WIN_BOOLEAN = 2;

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.END_GAME_MESSAGE.equals(messageType);
    }

    @Override
    public Message constructFromFields(final String[] fields) {
        getValues(fields);
        final EndGameMessage message = new EndGameMessage();
        message.setSenderId(readLong(fields[INDEX_USER_ID]));
        message.setWin(readBoolean(fields[INDEX_WIN_BOOLEAN]));
        return message;
    }
}
