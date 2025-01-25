package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.service.api.adapter.MessageAdapter;

public abstract class AbstractMessageAdapter implements MessageAdapter {

    protected void getValues(final String[] identifiersAndValues) {
        for (int i = 1; i < identifiersAndValues.length; i++) {
            identifiersAndValues[i] = identifiersAndValues[i].substring(identifiersAndValues[i].indexOf('=')+1);
        }
    }

    protected Long readLong(final String longString) {
        return Long.parseLong(longString);
    }

    protected Integer readInt(final String intString) {
        return Integer.parseInt(intString);
    }

    protected Double readDouble(final String doubleString) {
        return Double.parseDouble(doubleString);
    }

    protected Boolean readBoolean(final String booleanString) {
        return Boolean.parseBoolean(booleanString);
    }
}
