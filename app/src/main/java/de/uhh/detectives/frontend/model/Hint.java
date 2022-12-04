package de.uhh.detectives.frontend.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Hint implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int hintId;

    private String category;
    private String description;
    private Long possessorId;
    private Double longitude;
    private Double latitude;

    public Hint() {}

    @Ignore
    public Hint(final String stringToParse) {
        String[] tokens = stringToParse.split(";");
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].substring(tokens[i].indexOf("=") + 1);
        }
        this.category = tokens[0];
        this.description = tokens[1];
        this.possessorId = (tokens[2].equals("null")) ? null : Long.parseLong(tokens[2]);
        this.longitude = (tokens[3].equals("null")) ? null : Double.parseDouble(tokens[3]);
        this.latitude = (tokens[4].equals("null")) ? null : Double.parseDouble(tokens[4]);
    }

    public int getHintId() {
        return hintId;
    }

    public void setHintId(int hintId) {
        this.hintId = hintId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPossessorId(Long possessorId) {
        this.possessorId = possessorId;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Long getPossessorId() {
        return possessorId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}
