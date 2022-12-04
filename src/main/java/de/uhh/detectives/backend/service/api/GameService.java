package de.uhh.detectives.backend.service.api;

import de.uhh.detectives.backend.model.entity.Game;

public interface GameService {


    /**
     * Finds the active game for a user
     *
     * @param userId the id of the user, the game is searched for
     * @return active game that the user participates in or NULL if none is found
     */
    Game findActiveGameForUser(final Long userId);

    /**
     * Searches for a game that is neither started nor completed yet that users can register for
     *
     * @return boolean if a joinable game is available
     */
    boolean isJoinableGameAvailable();

    /**
     * Searches for a joinable game and registers the player for it.
     *
     * @param userId id of the player
     * @return the game that the player was registered for or NULL if any problems occur
     */
    Game registerPlayer(final Long userId);

    /**
     * generates a new game including a murder weapon, location and culprit and saves it in the database
     *
     * @param timestamp the timestamp will be used as id for the game
     */
    void generateGame(final Long timestamp);

    /**
     * After generating the hints for a game, the game is started the started game is returned.
     *
     * @param userIdOfStartingUser userId of user who wants to start the game
     * @param longitudeOfUser Longitude of user Location
     * @param latitudeOfUser Latitude of user Location
     * @return started game
     */
    Game startGame(final Long userIdOfStartingUser, final Double longitudeOfUser, final Double latitudeOfUser);

    /**
     * sets the completed value for a given game and persists it, so that the game is no longer regarded as active by
     * the findActiveGameForUser function.
     *
     * @param winnerId the id of the user that won the game
     * @return ended game or NULL if the user has no active game to end
     */
    Game endGame(final Long winnerId);
}
