package de.uhh.detectives.frontend.event;

public class RegisterEvent {
    public final String message;

    public RegisterEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
