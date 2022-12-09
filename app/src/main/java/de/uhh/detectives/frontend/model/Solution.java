package de.uhh.detectives.frontend.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Entity
public class Solution implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int solutionId;

    private String culprit;
    private String location;
    private String weapon;

    public Solution() {
    }

    public List<String> getSolutionList() {
        return Arrays.asList(weapon, culprit, location);
    }

    public int getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(int solutionId) {
        this.solutionId = solutionId;
    }

    public String getCulprit() {
        return culprit;
    }

    public void setCulprit(String culprit) {
        this.culprit = culprit;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }
}
