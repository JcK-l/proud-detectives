package de.uhh.detectives.backend.service.api.messaging;


/**
 * enum for the different Message types that the frontend might send
 */
public enum MessageType {
    UNKNOWN,
    CHAT_MESSAGE,
    REGISTER_MESSAGE,
    JOIN_GAME_MESSAGE,
    START_GAME_MESSAGE,
    END_GAME_MESSAGE,
    READY_MESSAGE,
    CLUES_GUESSES_STATE_MESSAGE
}
