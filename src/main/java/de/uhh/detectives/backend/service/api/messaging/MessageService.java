package de.uhh.detectives.backend.service.api.messaging;

import de.uhh.detectives.backend.model.messaging.Message;

public interface MessageService {

    boolean accepts(final MessageType messageType);
    String handle(final Message message);
}
