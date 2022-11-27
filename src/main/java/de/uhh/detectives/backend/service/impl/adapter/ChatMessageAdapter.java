package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.ChatMessage;
import de.uhh.detectives.backend.model.Message;

public class ChatMessageAdapter extends AbstractMessageAdapter {

    private static final int INDEX_SENDER_ID = 1;
    private static final int INDEX_RECEIVER_PSEUDONYM = 2;
    private static final int INDEX_MESSAGE_CONTENT = 3;
    private static final int INDEX_TIMESTAMP = 4;

    @Override
    public Message constructFromFields(final String[] fields) {
        getValues(fields);

        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(readLong(fields[INDEX_SENDER_ID]));
        chatMessage.setTimestamp(readLong(fields[INDEX_TIMESTAMP]));
        chatMessage.setReceiverPseudonym(fields[INDEX_RECEIVER_PSEUDONYM]);
        chatMessage.setMessageContent(fields[INDEX_MESSAGE_CONTENT]);
        return chatMessage;
    }
}
