package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.messaging.EmptyMessage;
import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.service.api.messaging.MessageService;
import de.uhh.detectives.backend.service.api.messaging.MessageType;
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
        final Message message = decipherMessage(messageString);
        for (final MessageService messageService : messageServices) {
            if (messageService.accepts(message.getType())) {
                LOG.info(String.format("processing message of type %s", message.getType()));
                return messageService.handle(message);
            }
        }
        LOG.error(String.format("Message of type %s could not be processed because there is no service defined for that message type!", message.getType()));
        return null;
    }

    private Message decipherMessage(final String messageString) {
        LOG.info("deciphering message");
        try {
            final String[] fields = messageString.split(";");
            final MessageType type = MessageType.valueOf(fields[0].substring(5));
            for (final MessageAdapter messageAdapter : messageAdapters) {
                if (messageAdapter.accepts(type)) {
                    return messageAdapter.constructFromFields(fields);
                }
            }
        // catching any exceptions since we don't want a client to close only because it sends one wrongly formatted message
        } catch (Exception e) {
            LOG.error(String.format("could not decipher message %s", messageString));
        }
        return new EmptyMessage();
    }
}
