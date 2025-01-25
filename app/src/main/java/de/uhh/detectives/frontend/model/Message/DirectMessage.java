package de.uhh.detectives.frontend.model.Message;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import de.uhh.detectives.frontend.model.Message.api.Message;

@Entity
public class DirectMessage implements Message {

    @PrimaryKey
    private Long id;

    private String pseudonym;

    private int position;

    public DirectMessage() {}

    public DirectMessage(String id, String pseudonym) {
        this.pseudonym = pseudonym;
        try {
            this.id = Long.parseLong(id);
        } catch (NumberFormatException e) {
            this.id = 0L;
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public MessageType getType() {
        return MessageType.UNKNOWN;
    }

    @NonNull
    @Override
    public String toString() {
        return "UNKNOWN: " + id;
    }
}
