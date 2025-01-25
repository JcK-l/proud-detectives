package de.uhh.detectives.frontend.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Player implements Serializable {

    @PrimaryKey
    private Long id;

    private String pseudonym;

    private boolean dead;

    public Player() {}

    // create Player
    @Ignore
    public Player(final Long id, final String pseudonym) {
       this.id = id;
       this.pseudonym = pseudonym;
    }

    // parse Player
    @Ignore
    public Player(final String stringToParse) {
        String[] tokens = stringToParse.split(";");
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].substring(tokens[i].indexOf("=") + 1);
        }
        this.id = Long.parseLong(tokens[0]);
        this.pseudonym = tokens[1];
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }
}
