package de.uhh.detectives.frontend.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

@Entity
public class ChatMessage implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int chatMessageId;

    private Long senderId;
    private Long receiverId;
    private String message;
    private Long timestamp;
    private String dateTime;
    private String pseudonym;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("hh:mm", Locale.ROOT);

    public ChatMessage() {}

    public ChatMessage(final Long userId, final String inputMessage ) {
        this.senderId = userId;

        final long currentTime = System.currentTimeMillis();
        this.timestamp = currentTime;

        this.dateTime = SDF.format(new Date(currentTime));
        this.message = inputMessage;
    }

    public ChatMessage(ParsedMessage parsedMessage) {
        this.senderId = parsedMessage.getSenderId();

        final long currentTime = System.currentTimeMillis();
        this.timestamp = currentTime;

        this.dateTime = SDF.format(new Date(currentTime));
        this.message = parsedMessage.getMessage();
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public int getChatMessageId() {
        return chatMessageId;
    }

    public void setChatMessageId(int chatMessageId) {
        this.chatMessageId = chatMessageId;
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

    @Override
    public String toString() {
        return "TYPE:CHAT_MESSAGE" +
                ";senderId=" + senderId +
                ";receiverId=" + receiverId +
                ";message=" + message +
                ";timestamp=" + timestamp +
                ";dateTime=" + dateTime;
    }
}
