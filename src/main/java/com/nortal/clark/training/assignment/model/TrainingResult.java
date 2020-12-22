package com.nortal.clark.training.assignment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@ToString
public class TrainingResult {
    @Getter
    private List<Position> capturedTargets = new ArrayList<>();
    // Time in millis representing training session period
    // In simulation mode this does not necessarily represent the time how long test was running
    @Getter
    private long trainingTime;
    @Getter
    @Setter
    private TrainingStatus trainingStatus;

    public void addCapturedTarget(Position capturedTarget) {
        if (!capturedTargets.contains(capturedTarget)) {
            this.capturedTargets.add(capturedTarget);
        }
    }

    public boolean isTrainingOver() {
        return EnumSet.of(TrainingStatus.OUTSIDE_CITY, TrainingStatus.COMPLETED, TrainingStatus.TIMEOUT).contains(trainingStatus);
    }

    public void addTrainingTime(long timeInMillisToAdd) {
        this.trainingTime += timeInMillisToAdd;
    }

    public void startTraining() {
        this.trainingTime = 0;
        this.trainingStatus = TrainingStatus.IN_CITY;
    }
}
