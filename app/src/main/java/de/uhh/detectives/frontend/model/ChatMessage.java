package de.uhh.detectives.frontend.model;

public class ChatMessage {
    private Long senderId;
    private Long receiverId;
    private String message;
    private Long timestamp;
    private String dateTime;

    public ChatMessage() {}

    public ChatMessage(final Long senderId, final Long timestamp) {
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int messageSize(){
        return message.length();
    }
}
