package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.Message;
import de.uhh.detectives.backend.model.Player;
import de.uhh.detectives.backend.model.RegisterMessage;
import de.uhh.detectives.backend.repository.PlayerRepository;
import de.uhh.detectives.backend.service.api.MessageType;
import de.uhh.detectives.backend.service.api.RegisterMessageService;
import de.uhh.detectives.backend.service.impl.adapter.RegisterMessageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegisterMessageServiceImpl implements RegisterMessageService {

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
        registerUser(player);
        return "ACKNOWLEDGED";
    }

    @Override
    public void registerUser(final Player player) {
        LOG.info("persist player into database");
        playerRepository.save(player);
    }
}
