package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.JoinGameMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Component;

@Component
public class JoinGameMessageAdapter extends AbstractMessageAdapter {

    private static final int INDEX_USER_ID = 1;

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.JOIN_GAME_MESSAGE.equals(messageType);
    }

    @Override
    public Message constructFromFields(final String[] fields) {
        getValues(fields);
        final JoinGameMessage message = new JoinGameMessage();
        message.setSenderId(readLong(fields[INDEX_USER_ID]));
        return message;
    }
}
