package de.uhh.detectives.frontend.model.event;

import de.uhh.detectives.frontend.model.Message.ChatMessage;
import de.uhh.detectives.frontend.model.Message.MessageType;
import de.uhh.detectives.frontend.model.event.api.MessageEvent;

public class ChatMessageEvent implements MessageEvent {

    public ChatMessage message;

    public ChatMessageEvent() {}

    public ChatMessageEvent(ChatMessage message) {
        this.message = message;
    }

    public ChatMessage getMessage() {
        return message;
    }

    @Override
    public MessageEvent generateMessageEvent(String input) {
        return new ChatMessageEvent(new ChatMessage(input));
    }

    @Override
    public boolean accepts(MessageType messageType) {
        return messageType == MessageType.CHAT_MESSAGE;
    }
}
