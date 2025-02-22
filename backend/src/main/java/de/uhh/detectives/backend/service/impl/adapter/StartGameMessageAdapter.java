package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.messaging.StartGameMessage;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import org.springframework.stereotype.Component;

@Component
public class StartGameMessageAdapter extends AbstractMessageAdapter {

    private static final int INDEX_USER_ID = 1;
    private static final int INDEX_LONGITUDE = 2;
    private static final int INDEX_LATITUDE = 3;
    private static final int INDEX_PLAYING_AREA_RADIUS = 4;

    @Override
    public boolean accepts(final MessageType type) {
        return MessageType.START_GAME_MESSAGE.equals(type);
    }

    @Override
    public Message constructFromFields(final String[] fields) {
        getValues(fields);

        final StartGameMessage startGameMessage = new StartGameMessage();
        startGameMessage.setUserId(readLong(fields[INDEX_USER_ID]));
        startGameMessage.setLongitude(readDouble(fields[INDEX_LONGITUDE]));
        startGameMessage.setLatitude(readDouble(fields[INDEX_LATITUDE]));
        if (fields.length > 4) {
            startGameMessage.setRadius(readInt(fields[INDEX_PLAYING_AREA_RADIUS]));
        }
        return startGameMessage;
    }
}
