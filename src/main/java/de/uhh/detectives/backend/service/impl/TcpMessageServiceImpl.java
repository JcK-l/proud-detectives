package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.ChatMessage;
import de.uhh.detectives.backend.model.Message;
import de.uhh.detectives.backend.service.api.ChatMessageService;
import de.uhh.detectives.backend.service.api.TcpMessageService;
import de.uhh.detectives.backend.service.api.MessageType;
import de.uhh.detectives.backend.service.api.adapter.MessageAdapter;
import de.uhh.detectives.backend.service.impl.adapter.ChatMessageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TcpMessageServiceImpl implements TcpMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(TcpMessageServiceImpl.class);

    private ChatMessageService chatMessageService;

    private final MessageAdapter chatMessageAdapter = new ChatMessageAdapter();

    public TcpMessageServiceImpl(final ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @Override
    public void receiveMessage(final String messageString) {
        LOG.info("deciphering message: " + messageString);
        final Message message = decipherMessage(messageString);
        // TODO refactor, when we have multiple implementation of Messages
        chatMessageService.saveMessage((ChatMessage)message);
    }

    private Message decipherMessage(final String messageString) {
        final String[] fields = messageString.split(";");
        final MessageType type = MessageType.valueOf(fields[0].substring(5));
        return switch (type) {
            case CHAT_MESSAGE -> chatMessageAdapter.constructFromFields(fields);
            default -> null;
        };
    }
}
