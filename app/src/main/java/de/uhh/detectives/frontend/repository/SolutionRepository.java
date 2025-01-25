package de.uhh.detectives.frontend.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import de.uhh.detectives.frontend.model.Solution;

@Dao
public interface SolutionRepository {

    @Query("DELETE FROM Solution")
    void deleteAll();

    @Insert
    void insert(final Solution solution);

    @Query("SELECT * FROM Solution LIMIT 1")
    Solution findFirst();
}
