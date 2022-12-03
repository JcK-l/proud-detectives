package de.uhh.detectives.frontend.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class UserData implements Serializable {
    @PrimaryKey
    private long userId;

    private String pseudonym;
    private String prename;
    private String surname;

    public UserData() {
    }

    public UserData(long userId, String pseudonym, String prename, String surname) {
        this.userId = userId;
        this.pseudonym = pseudonym;
        this.prename = prename;
        this.surname = surname;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(final long userId) {
        this.userId = userId;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(final String pseudonym) {
        this.pseudonym = pseudonym;
    }
}
