package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.ChatMessage;
import de.uhh.detectives.backend.model.Message;

public class ChatMessageAdapter extends AbstractMessageAdapter {

    private static final int INDEX_MESSAGE_TYPE = 0;
    private static final String STRING_TYPE = "TYPE:";
    private static final int INDEX_SENDER_ID = 1;
    private static final String STRING_SENDER_ID = "senderId";
    private static final int INDEX_RECEIVER_PSEUDONYM = 2;
    private static final String STRING_RECEIVER_PSEUDONYM = "receiverId";
    private static final int INDEX_MESSAGE_CONTENT = 3;
    private static final String STRING_MESSAGE_CONTENT = "message";
    private static final int INDEX_TIMESTAMP = 4;
    private static final String STRING_TIMESTAMP = "timestamp";

    private static final char IDENTIFIER_VALUE_SEPERATOR = '=';
    private static final String FIELD_DELIMITER = ";";

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

    public String toBroadcastString(final ChatMessage message) {
        final String[] fields = new String[5];
        fields[INDEX_MESSAGE_TYPE] = STRING_TYPE + message.getType();
        fields[INDEX_SENDER_ID] = STRING_SENDER_ID + IDENTIFIER_VALUE_SEPERATOR + message.getSenderId();
        fields[INDEX_RECEIVER_PSEUDONYM] = STRING_RECEIVER_PSEUDONYM + IDENTIFIER_VALUE_SEPERATOR + message.getReceiverPseudonym();
        fields[INDEX_MESSAGE_CONTENT] = STRING_MESSAGE_CONTENT + IDENTIFIER_VALUE_SEPERATOR + message.getMessageContent();
        fields[INDEX_TIMESTAMP] = STRING_TIMESTAMP + IDENTIFIER_VALUE_SEPERATOR + message.getTimestamp();
        return String.join(FIELD_DELIMITER, fields);
    }
}
