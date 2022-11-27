package de.uhh.detectives.backend.service.api.adapter;

import de.uhh.detectives.backend.model.Message;

public interface MessageAdapter {
    Message constructFromFields(final String[] fields);
}
