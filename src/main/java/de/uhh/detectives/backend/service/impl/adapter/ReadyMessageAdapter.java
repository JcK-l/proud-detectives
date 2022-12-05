package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.ReadyMessage;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Component;

@Component
public class ReadyMessageAdapter extends AbstractMessageAdapter {

    private static final int INDEX_USER_ID = 1;
    private static final int INDEX_READY_FLAG = 2;

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.READY_MESSAGE.equals(messageType);
    }

    @Override
    public Message constructFromFields(final String[] fields) {
        getValues(fields);
        final ReadyMessage message = new ReadyMessage();
        message.setSenderId(readLong(fields[INDEX_USER_ID]));
        message.setReady(readBoolean(fields[INDEX_READY_FLAG]));
        return message;
    }
}
