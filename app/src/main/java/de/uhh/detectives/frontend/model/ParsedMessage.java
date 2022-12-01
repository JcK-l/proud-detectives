package de.uhh.detectives.frontend.model;

public class ParsedMessage {
    private Long senderId;
    private String message;
    private String pseudonym;
    private MessageType messageType;

    public ParsedMessage(String messageSent){
        parseMessage(messageSent);
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    private void parseMessage(String messageSent) {
        String[] tokens = messageSent.split(";");

        switch (tokens[0].substring(5)) {
            case "CHAT_MESSAGE":
                this.messageType = MessageType.CHAT_MESSAGE;
                break;
            case "REGISTER_MESSAGE":
                this.messageType = MessageType.REGISTER_MESSAGE;
                break;
            case "UNKNOWN":
                this.messageType = MessageType.UNKNOWN;
                break;
        }
        this.senderId = Long.parseLong(tokens[1].substring(9));
        this.message = tokens[3].substring(8);
        this.pseudonym = tokens[5].substring(16);
    }
}
