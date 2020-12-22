package com.nortal.clark.training.assignment.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class CityMap implements Serializable {

    // City size
    private Position maximum;
    @Getter
    private List<Position> targets = new ArrayList<>();

    public CityMap(Position maximum) {
        this.maximum = maximum;
    }

    public void addTarget(int x, int y) {
        targets.add(new Position(x, y));
    }

    public int getHeight() {
        return maximum.y;
    }

    public int getWidth() {
        return maximum.x;
    }

    public boolean isWithinCity(Position position) {
        return position.x > 0 && position.x < maximum.x && position.y > 0 && position.y < maximum.y;
    }
}
