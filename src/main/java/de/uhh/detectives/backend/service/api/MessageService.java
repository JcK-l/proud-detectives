package de.uhh.detectives.backend.service.api;

import de.uhh.detectives.backend.model.Message;

public interface MessageService {

    String handle(final Message message);
}
