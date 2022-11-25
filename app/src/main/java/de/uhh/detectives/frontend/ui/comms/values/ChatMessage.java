package de.uhh.detectives.frontend.ui.comms.values;

public class ChatMessage {
    public Long senderId;
    public Long receiverId;
    public String message;
    public String dateTime;

    public int size(){
        return message.length();
    }
}
