package com.nortal.clark.training.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class VoiceCommand implements Serializable {

    private Direction direction = Direction.EAST;
    private SpeedLevel speedLevel = SpeedLevel.L0_RUNNING_HUMAN;

}
