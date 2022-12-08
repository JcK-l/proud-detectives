package de.uhh.detectives.frontend.model.Message;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import de.uhh.detectives.frontend.model.Message.api.Message;
import de.uhh.detectives.frontend.model.UserData;

@Entity
public class ChatMessage implements Serializable, Message {
    @PrimaryKey(autoGenerate = true)
    private int chatMessageId;

    private Long senderId;

    private Long receiverId;
    private String message;
    private Long timestamp;
    private String dateTime;
    private String pseudonym;
    private boolean dead;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("hh:mm", Locale.ROOT);

    public ChatMessage() {}

    // Create ChatMessage
    @Ignore
    public ChatMessage(final UserData user, @Nullable Long receiverId, final String message) {
        this.senderId = user.getUserId();
        this.pseudonym = user.getPseudonym();
        this.receiverId = receiverId;
        this.message = message;

        final long currentTime = System.currentTimeMillis();
        this.timestamp = currentTime;

        this.dateTime = SDF.format(new Date(currentTime));
    }

    // Parse ChatMessage
    @Ignore
    public ChatMessage(String messageToParse) {
        String[] tokens = messageToParse.split(";");
        for (int i = 1; i < tokens.length; i++) {
            tokens[i] = tokens[i].substring(tokens[i].indexOf("=") + 1);
        }

        this.senderId = Long.parseLong(tokens[1]);
        this.receiverId = (tokens[2].equals("null")) ? null : Long.parseLong(tokens[2]);
        this.message = tokens[3];
        this.timestamp = Long.parseLong(tokens[4]);
        this.pseudonym = tokens[5];
        this.dead = Boolean.parseBoolean(tokens[6]);
        final long currentTime = System.currentTimeMillis();
        this.dateTime = SDF.format(new Date(currentTime));
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public int getChatMessageId() {
        return chatMessageId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setChatMessageId(int chatMessageId) {
        this.chatMessageId = chatMessageId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
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

    @Override
    public MessageType getType(){
        return MessageType.CHAT_MESSAGE;
    }
}
