package de.uhh.detectives.frontend.model.Message;

import androidx.annotation.Nullable;

import de.uhh.detectives.frontend.model.Message.api.Message;
import de.uhh.detectives.frontend.model.UserData;

public class RegisterMessage implements Message {
    // for sending
    @Nullable private Long userId;
    @Nullable private String pseudonym;
    @Nullable private String prename;
    @Nullable private String surname;

    // for receiving
    private int status;
    @Nullable private String result;

    public RegisterMessage() {}

    // Create RegisterMessage
    public RegisterMessage(final UserData user) {
        this.userId = user.getUserId();
        this.pseudonym = user.getPseudonym();
        this.prename = user.getPrename();
        this.surname = user.getSurname();

        this.status = 0;
        this.result = null;
    }

    // Parse RegisterMessage
    public RegisterMessage(String messageToParse) {
        String[] tokens = messageToParse.split(";");
        for (int i = 1; i < tokens.length; i++) {
            tokens[i] = tokens[i].substring(tokens[i].indexOf("=") + 1);
        }

        try {
            this.status = Integer.parseInt(tokens[1]);
            this.result = tokens[2];
        } catch (Exception ignored) {
            this.status = 0;
            this.result = null;
        }
        this.userId = null;
        this.pseudonym = null;
        this.prename = null;
        this.surname = null;
    }

    @Nullable
    public Long getUserId() {
        return userId;
    }

    @Nullable
    public String getPseudonym() {
        return pseudonym;
    }

    @Nullable
    public String getPrename() {
        return prename;
    }

    @Nullable
    public String getSurname() {
        return surname;
    }

    public int getStatus() {
        return status;
    }

    @Nullable
    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "TYPE:REGISTER_MESSAGE" +
                ";userId=" + this.userId +
                ";pseudonym=" + this.pseudonym +
                ";prename=" + this.prename +
                ";surname=" + this.surname;
    }

    @Override
    public MessageType getType(){
       return MessageType.REGISTER_MESSAGE;
    }
}
