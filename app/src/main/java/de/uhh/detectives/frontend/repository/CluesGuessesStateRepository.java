package de.uhh.detectives.frontend.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.uhh.detectives.frontend.model.CluesGuessesState;
import de.uhh.detectives.frontend.ui.clues_and_guesses.Cell;

@Dao
public interface CluesGuessesStateRepository {

    @Query("DELETE FROM CluesGuessesState")
    void deleteAll();

    @Query("SELECT * FROM CLUESGUESSESSTATE WHERE playerId == :id")
    CluesGuessesState findFromId(Long id);

    @Insert
    void insert(final CluesGuessesState cluesGuessesState);

    @Query("SELECT * FROM CluesGuessesState")
    List<CluesGuessesState> getAll();

    @Query("SELECT * FROM CluesGuessesState LIMIT 1")
    CluesGuessesState findFirst();

    @Query("UPDATE CluesGuessesState SET cells = :cells, cardColor = :cardColor, numberOfTries = :numberOfTries," +
            "suspicionLeft = :suspicionLeft, suspicionMiddle = :suspicionMiddle, suspicionRight = :suspicionRight WHERE playerId =:id")
    void updateAll(List<Cell> cells, int cardColor, int numberOfTries,
                   int suspicionLeft, int suspicionMiddle, int suspicionRight, Long id);

    @Query("UPDATE CluesGuessesState SET cells = :cells WHERE playerId =:id")
    void updateCells(List<Cell> cells, Long id);

    @Query("UPDATE CluesGuessesState SET cardColor = :cardColor WHERE playerId =:id")
    void updateCardColor(int cardColor, Long id);

    @Query("UPDATE CluesGuessesState SET numberOfTries = numberOfTries + 1 WHERE playerId =:id")
    void updateNumberOfTries(Long id);

    @Query("UPDATE CluesGuessesState SET suspicionLeft = :suspicion WHERE playerId =:id")
    void updateSuspicionLeft(int suspicion, Long id);

    @Query("UPDATE CluesGuessesState SET suspicionLeftTag = :tag WHERE playerId =:id")
    void updateSuspicionLeft(String tag, Long id);

    @Query("UPDATE CluesGuessesState SET suspicionMiddle = :suspicion WHERE playerId =:id")
    void updateSuspicionMiddle(int suspicion, Long id);

    @Query("UPDATE CluesGuessesState SET suspicionMiddleTag = :tag WHERE playerId =:id")
    void updateSuspicionMiddle(String tag, Long id);

    @Query("UPDATE CluesGuessesState SET suspicionRight = :suspicion WHERE playerId =:id")
    void updateSuspicionRight(int suspicion, Long id);

    @Query("UPDATE CluesGuessesState SET suspicionRightTag = :tag WHERE playerId =:id")
    void updateSuspicionRight(String tag, Long id);
}
