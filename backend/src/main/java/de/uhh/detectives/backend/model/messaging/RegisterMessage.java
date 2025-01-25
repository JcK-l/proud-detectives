package de.uhh.detectives.backend.model.messaging;

import de.uhh.detectives.backend.service.api.messaging.MessageType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
public class RegisterMessage implements Serializable, Message {

    @Serial
    private static final long serialVersionUID = -9218410689175558493L;

    private Long userId;

    private String pseudonym;

    private String prename;

    private String surname;


    @Override
    public MessageType getType() {
        return MessageType.REGISTER_MESSAGE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterMessage that = (RegisterMessage) o;
        return getUserId().equals(that.getUserId()) && getPseudonym().equals(that.getPseudonym()) &&
                Objects.equals(getPrename(), that.getPrename()) && Objects.equals(getSurname(), that.getSurname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getPseudonym(), getPrename(), getSurname());
    }
}
