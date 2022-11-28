package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.EmptyMessage;
import de.uhh.detectives.backend.model.Message;
import de.uhh.detectives.backend.service.api.MessageService;
import de.uhh.detectives.backend.service.api.MessageType;
import de.uhh.detectives.backend.service.api.TcpMessageService;
import de.uhh.detectives.backend.service.api.adapter.MessageAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TcpMessageServiceImpl implements TcpMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(TcpMessageServiceImpl.class);

    private final List<MessageService> messageServices;

    private final List<MessageAdapter> messageAdapters;

    public TcpMessageServiceImpl(final List<MessageService> messageServices,
                                 final List<MessageAdapter> messageAdapters) {
        this.messageServices = messageServices;
        this.messageAdapters = messageAdapters;
    }

    @Override
    public String receiveMessage(final String messageString) {
        LOG.info("deciphering message: " + messageString);
        final Message message = decipherMessage(messageString);
        for (final MessageService messageService : messageServices) {
            if (messageService.accepts(message.getType())) {
                return messageService.handle(message);
            }
        }
        LOG.error(String.format("Message of type %s could not be processed because there is no service defined for that message type!", message.getType()));
        return null;
    }

    private Message decipherMessage(final String messageString) {
        try {
            final String[] fields = messageString.split(";");
            final MessageType type = MessageType.valueOf(fields[0].substring(5));
            for (final MessageAdapter messageAdapter : messageAdapters) {
                if (messageAdapter.accepts(type)) {
                    return messageAdapter.constructFromFields(fields);
                }
            }
        } catch (IllegalArgumentException e) {
            LOG.error(String.format("could not decipher message %s", messageString));
        }
        return new EmptyMessage();
    }
}
