package de.uhh.detectives.frontend.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class UserData implements Serializable {
    @PrimaryKey
    private long userId;

    private String nickname;

    public UserData() {
        this.userId = System.currentTimeMillis();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(final long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }
}
