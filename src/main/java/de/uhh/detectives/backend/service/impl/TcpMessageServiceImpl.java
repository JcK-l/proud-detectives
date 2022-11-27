package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.Message;
import de.uhh.detectives.backend.service.api.ChatMessageService;
import de.uhh.detectives.backend.service.api.MessageType;
import de.uhh.detectives.backend.service.api.RegisterMessageService;
import de.uhh.detectives.backend.service.api.TcpMessageService;
import de.uhh.detectives.backend.service.api.adapter.MessageAdapter;
import de.uhh.detectives.backend.service.impl.adapter.ChatMessageAdapter;
import de.uhh.detectives.backend.service.impl.adapter.RegisterMessageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TcpMessageServiceImpl implements TcpMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(TcpMessageServiceImpl.class);

    private final ChatMessageService chatMessageService;
    private final RegisterMessageService registerMessageService;

    private final MessageAdapter chatMessageAdapter = new ChatMessageAdapter();
    private final RegisterMessageAdapter registerMessageAdapter = new RegisterMessageAdapter();

    public TcpMessageServiceImpl(final ChatMessageService chatMessageService,
                                 final RegisterMessageService registerMessageService) {
        this.chatMessageService = chatMessageService;
        this.registerMessageService = registerMessageService;
    }

    @Override
    public String receiveMessage(final String messageString) {
        LOG.info("deciphering message: " + messageString);
        final Message message = decipherMessage(messageString);
        return switch (message.getType()) {
            case CHAT_MESSAGE -> chatMessageService.handle(message);
            case REGISTER_MESSAGE -> registerMessageService.handle(message);
            default -> null;
        };
    }

    private Message decipherMessage(final String messageString) {
        final String[] fields = messageString.split(";");
        final MessageType type = MessageType.valueOf(fields[0].substring(5));
        return switch (type) {
            case CHAT_MESSAGE -> chatMessageAdapter.constructFromFields(fields);
            case REGISTER_MESSAGE -> registerMessageAdapter.constructFromFields(fields);
            default -> null;
        };
    }
}
