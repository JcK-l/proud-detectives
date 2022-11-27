package de.uhh.detectives.backend.service.api;

import de.uhh.detectives.backend.model.Player;

public interface RegisterMessageService extends MessageService {

    void registerUser(final Player player);
}
