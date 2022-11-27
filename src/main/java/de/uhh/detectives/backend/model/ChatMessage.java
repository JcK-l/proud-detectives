package de.uhh.detectives.backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Entity
public class ChatMessage implements Serializable, Message {

    @Serial
    private static final long serialVersionUID = 7125610720964784995L;

    private Long messageId;
    private Long senderId;
    private String receiverPseudonym;
    private Long timestamp;
    private String messageContent;

    @Id
    @GeneratedValue
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    @NotNull
    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getReceiverPseudonym() {
        return receiverPseudonym;
    }

    public void setReceiverPseudonym(String receiverPseudonym) {
        this.receiverPseudonym = receiverPseudonym;
    }

    @NotNull
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @NotNull
    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessage that = (ChatMessage) o;

        if (getMessageId() != null ? !getMessageId().equals(that.getMessageId()) : that.getMessageId() != null)
            return false;
        if (!getSenderId().equals(that.getSenderId())) return false;
        if (getReceiverPseudonym() != null ? !getReceiverPseudonym().equals(that.getReceiverPseudonym()) : that.getReceiverPseudonym() != null)
            return false;
        if (!getTimestamp().equals(that.getTimestamp())) return false;
        return getMessageContent().equals(that.getMessageContent());
    }

    @Override
    public int hashCode() {
        int result = getMessageId() != null ? getMessageId().hashCode() : 0;
        result = 31 * result + getSenderId().hashCode();
        result = 31 * result + (getReceiverPseudonym() != null ? getReceiverPseudonym().hashCode() : 0);
        result = 31 * result + getTimestamp().hashCode();
        result = 31 * result + getMessageContent().hashCode();
        return result;
    }
}
