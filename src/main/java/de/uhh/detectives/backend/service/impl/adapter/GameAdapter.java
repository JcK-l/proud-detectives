package de.uhh.detectives.backend.service.impl.adapter;

import de.uhh.detectives.backend.model.Hint;
import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import org.springframework.stereotype.Component;

@Component
public class GameAdapter {

    private static final String GAME_ID_STRING = "gameId";
    private static final String CULPRIT_STRING = "culprit";
    private static final String LOCATION_STRING = "location";
    private static final String WEAPON_STRING = "weapon";

    private static final String PLAYER_ID_STRING = "id";
    private static final String PLAYER_PSEUDONYM_STRING = "pseudonym";

    private static final String HINT_CATEGORY_STRING = "category";
    private static final String HINT_LABEL_STRING = "description";
    private static final String HINT_POSSESSOR_ID_STRING = "possessorId";
    private static final String HINT_LONGITUDE_STRING = "longitude";
    private static final String HINT_LATITUDE_STRING = "latitude";

    private static final char IDENTIFIER_VALUE_SEPERATOR = '=';
    private static final String FIELD_DELIMITER = ";";

    public String serialize(final Game game) {
        final StringBuilder builder = new StringBuilder();
        appendValueForIdentifier(builder, GAME_ID_STRING, game.getGameId());
        appendValueForIdentifier(builder, CULPRIT_STRING, game.getCulprit());
        appendValueForIdentifier(builder, LOCATION_STRING, game.getLocation());
        appendValueForIdentifier(builder, WEAPON_STRING, game.getWeapon());

        // append players
        builder.append("Players[");
        for (final Participant player : game.getParticipants()) {
            appendPlayer(builder, player);
        }
        builder.append("]");

        // append hints
        builder.append("Hints[");
        for (final Hint hint : game.getHints()) {
            appendHint(builder, hint);
        }
        builder.append("]");

        return builder.toString();
    }

    private void appendPlayer(final StringBuilder builder, final Participant participant) {
        appendValueForIdentifier(builder, PLAYER_ID_STRING, participant.getPlayer().getId());
        appendValueForIdentifier(builder, PLAYER_PSEUDONYM_STRING, participant.getPlayer().getPseudonym());
    }

    private void appendHint(final StringBuilder builder, final Hint hint) {
        appendValueForIdentifier(builder, HINT_CATEGORY_STRING, hint.getItem().getCategory());
        appendValueForIdentifier(builder, HINT_LABEL_STRING, hint.getItem().getLabel());
        final Long possessorId = hint.getPossessor() == null ? null : hint.getPossessor().getId();
        appendValueForIdentifier(builder, HINT_POSSESSOR_ID_STRING, possessorId);
        appendValueForIdentifier(builder, HINT_LONGITUDE_STRING, hint.getLongitude());
        appendValueForIdentifier(builder, HINT_LATITUDE_STRING, hint.getLatitude());
    }

    private void appendValueForIdentifier(final StringBuilder builder, final String identifier, final Long longValue) {
        final String value = longValue == null ? "null" : longValue.toString();
        appendValueForIdentifier(builder, identifier, value);
    }

    private void appendValueForIdentifier(final StringBuilder builder, final String identifier, final Double doubleValue) {
        final String value = doubleValue == null ? "null" : doubleValue.toString();
        appendValueForIdentifier(builder, identifier, value);
    }

    private void appendValueForIdentifier(final StringBuilder builder, final String identifier, final String value) {
        builder.append(identifier).append(IDENTIFIER_VALUE_SEPERATOR).append(value).append(FIELD_DELIMITER);
    }
}
