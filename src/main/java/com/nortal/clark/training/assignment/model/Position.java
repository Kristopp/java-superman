package com.nortal.clark.training.assignment.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Position {
    public int x;
    public int y;

    public Position(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }
}
