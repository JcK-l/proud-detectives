package de.uhh.detectives.frontend.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Map implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int mapId;

    private double centerX;
    private double centerY;
    private double radius;

    public Map(double centerX, double centerY, double radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public int getMapId() {
        return mapId;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getRadius() {
        return radius;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
}
