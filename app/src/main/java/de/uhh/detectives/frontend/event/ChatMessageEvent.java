package de.uhh.detectives.frontend.event;

import de.uhh.detectives.frontend.model.ChatMessage;

public class ChatMessageEvent {

    public final ChatMessage message;

    public ChatMessageEvent(ChatMessage message) {
        this.message = message;
    }

    public ChatMessage getMessage() {
        return message;
    }
}
