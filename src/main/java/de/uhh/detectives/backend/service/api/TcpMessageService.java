package de.uhh.detectives.backend.service.api;

public interface TcpMessageService {

    /**
     * Takes a message from the TCP client.
     * Messages have to be Strings of the following format:
     * starting with 'TYPE:MESSAGE_TYPE;'
     * MESSAGE_TYPE has to be one of EnumValues of MessageType.enum
     * then the properties have to be mapped like follows:
     * 'identifier=value'
     *
     * @param message the message that was sent from the client
     * @return a return message as String from the server or NULL
     */
    String receiveMessage(final String message);

}
