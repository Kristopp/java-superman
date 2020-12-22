package com.nortal.clark.training.assignment.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum Direction implements Serializable {

    NORTH(1),
    EAST(1),
    SOUTH(-1),
    WEST(-1);

    /**
     * Acceleration modifier indicates how specific direction affects super-kid acceleration.
     * <ul>
     * <li>If WEST direction is used, superman moves to the left across the map.</li>
     * <li>If NORTH direction is used, superman moves up across the map.</li>
     * <li>If EAST direction is used, superman moves to the right across the map.</li>
     * <li>If SOUTH direction is used, superman moves down across the map.</li>
     * </ul>
     */
    @Getter
    private int accelerationModifier;
}
