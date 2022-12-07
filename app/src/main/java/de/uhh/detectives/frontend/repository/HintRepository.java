package de.uhh.detectives.frontend.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.uhh.detectives.frontend.model.Hint;

@Dao
public interface HintRepository {

    @Query("DELETE FROM Hint")
    void deleteAll();

    @Insert
    void insertAll(final List<Hint> hints);

    @Insert
    void insert(final Hint hint);

    @Query("SELECT * FROM Hint")
    List<Hint> getAll();

    @Query("SELECT * FROM Hint LIMIT 1")
    Hint findFirst();

    @Query("SELECT * FROM Hint WHERE description= :id")
    List<Hint> findHintsById(String id);
}
