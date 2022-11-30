package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.Message;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.RegisterMessage;
import de.uhh.detectives.backend.service.api.MessageType;
import org.springframework.stereotype.Component;

@Component
public class RegisterMessageAdapter extends AbstractMessageAdapter {

    private static final int INDEX_USER_ID = 1;
    private static final int INDEX_PSEUDONYM = 2;
    private static final int INDEX_PRENAME = 3;
    private static final int INDEX_SURNAME = 4;

    @Override
    public boolean accepts(final MessageType type) {
        return MessageType.REGISTER_MESSAGE.equals(type);
    }

    @Override
    public Message constructFromFields(final String[] fields) {
        getValues(fields);

        final RegisterMessage registerMessage = new RegisterMessage();
        registerMessage.setUserId(readLong(fields[INDEX_USER_ID]));
        registerMessage.setPseudonym(fields[INDEX_PSEUDONYM]);
        registerMessage.setPrename(fields[INDEX_PRENAME]);
        registerMessage.setSurname(fields[INDEX_SURNAME]);
        return registerMessage;
    }

    public Player createPlayerFromMessage(final RegisterMessage registerMessage){
        final Player player = new Player();
        player.setId(registerMessage.getUserId());
        player.setPrename(registerMessage.getPrename());
        player.setSurname(registerMessage.getSurname());
        player.setPseudonym(registerMessage.getPseudonym());
        return player;
    }
}
