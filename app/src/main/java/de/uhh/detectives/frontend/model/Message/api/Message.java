package de.uhh.detectives.frontend.model.Message.api;

import de.uhh.detectives.frontend.model.Message.MessageType;

public interface Message {
    String toString();

    MessageType getType();
}
