package de.uhh.detectives.frontend.model.Message.api;

import androidx.annotation.NonNull;

import de.uhh.detectives.frontend.model.Message.MessageType;

public interface Message {
    @NonNull
    String toString();

    MessageType getType();
}
