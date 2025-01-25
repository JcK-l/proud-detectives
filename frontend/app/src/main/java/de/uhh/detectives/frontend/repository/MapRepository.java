package de.uhh.detectives.frontend.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import de.uhh.detectives.frontend.model.Map;


@Dao
public interface MapRepository {
    @Insert
    void insert(final Map map);

    @Query("SELECT * FROM Map")
    Map getAll();
}
