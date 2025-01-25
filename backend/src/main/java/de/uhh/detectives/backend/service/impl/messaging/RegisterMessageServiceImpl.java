package de.uhh.detectives.backend.service.impl.messaging;

import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.messaging.RegisterMessage;
import de.uhh.detectives.backend.repository.PlayerRepository;
import de.uhh.detectives.backend.service.api.messaging.MessageService;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
import de.uhh.detectives.backend.service.impl.adapter.RegisterMessageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegisterMessageServiceImpl implements MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterMessageServiceImpl.class);

    private final RegisterMessageAdapter registerMessageAdapter;
    private final PlayerRepository playerRepository;

    public RegisterMessageServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.registerMessageAdapter = new RegisterMessageAdapter();
    }

    @Override
    public boolean accepts(final MessageType messageType) {
        return MessageType.REGISTER_MESSAGE.equals(messageType);
    }

    @Override
    public String handle(final Message message) {
        final RegisterMessage registerMessage = (RegisterMessage) message;
        final Player player =  registerMessageAdapter.createPlayerFromMessage(registerMessage);
        LOG.info("persist player into database");
        playerRepository.save(player);
        return "TYPE:" + MessageType.REGISTER_MESSAGE + ";status=200;result=ACKNOWLEDGED";
    }
}
