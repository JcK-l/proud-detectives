package de.uhh.detectives.backend.model.messaging;

import de.uhh.detectives.backend.service.api.messaging.MessageType;

public interface Message {

    MessageType getType();
}
