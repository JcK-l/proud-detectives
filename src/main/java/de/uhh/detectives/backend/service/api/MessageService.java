package de.uhh.detectives.backend.service.api;

import de.uhh.detectives.backend.model.Message;

public interface MessageService {

    boolean accepts(final MessageType messageType);
    String handle(final Message message);
}
