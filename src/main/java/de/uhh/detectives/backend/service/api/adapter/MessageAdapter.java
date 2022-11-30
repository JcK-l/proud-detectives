package de.uhh.detectives.backend.service.api.adapter;

import de.uhh.detectives.backend.model.messaging.Message;
import de.uhh.detectives.backend.service.api.messaging.MessageType;

public interface MessageAdapter {
    boolean accepts(final MessageType type);
    Message constructFromFields(final String[] fields);
}
