package com.nortal.clark.training.assignment.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Clark implements Serializable {

    /**
     * Positive speed = moving east
     * Negative speed = moving west
     */
    private double horizontal = 0;
    /**
     * Positive speed = moving north
     * Negative speed = moving south
     */
    private double vertical = 0;
    private Position position = new Position(1, 1);

    @Override
    public String toString() {
        return String.format("Clark {horizontal=%s, vertical=%s, position=(%s ; %s)}",
                horizontal,
                vertical,
                position.x,
                position.y);
    }
}

