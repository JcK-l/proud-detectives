package de.uhh.detectives.frontend.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import de.uhh.detectives.frontend.model.UserData;

@Dao
public interface UserDataRepository {

    @Insert
    void insertAll(final UserData... users);

    @Query("SELECT * FROM UserData")
    List<UserData> getAll();

    @Query("SELECT * FROM UserData LIMIT 1")
    UserData checkEmpty();
}
