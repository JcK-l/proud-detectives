package de.uhh.detectives.backend.service.api;

import de.uhh.detectives.backend.model.entity.Player;

public interface RegisterMessageService extends MessageService {

    void registerUser(final Player player);
}
