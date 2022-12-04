package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.WinGameMessage;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Component;

@Component
public class WinGameMessageAdapter extends AbstractMessageAdapter {

    private static final int INDEX_USER_ID = 1;

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.WIN_GAME_MESSAGE.equals(messageType);
    }

    @Override
    public Message constructFromFields(final String[] fields) {
        getValues(fields);
        final WinGameMessage message = new WinGameMessage();
        message.setSenderId(readLong(fields[INDEX_USER_ID]));
        return message;
    }
}
