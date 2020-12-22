package com.nortal.clark.training.simulator;

import com.nortal.clark.training.assignment.model.Clark;
import com.nortal.clark.training.assignment.model.Direction;
import com.nortal.clark.training.assignment.model.SpeedLevel;
import com.nortal.clark.training.assignment.model.VoiceCommand;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class TrainingSimulatorSpeedCalculationTest {

    private TrainingSimulator simulator = new TrainingSimulator();

    @Test
    void testCalculateNewSpeedFormula() {
        int currentSpeed = 1;
        int acceleration = -1;
        int timeSpentSeconds = 1;

        double speed = simulator.calculateNewSpeed(currentSpeed, acceleration, timeSpentSeconds);
        assertThat(speed, equalTo(0d));

        timeSpentSeconds = 2;
        speed = simulator.calculateNewSpeed(currentSpeed, acceleration, timeSpentSeconds);
        assertThat(speed, equalTo(-1d));
    }

    @Test
    void testCalculateNewSpeedWithDragWhileStopping() {
        int currentSpeed = 0;
        int timeSpentSeconds = 9999;

        double speed = simulator.calculateNewSpeedWithDrag(currentSpeed, timeSpentSeconds);
        assertThat(speed, equalTo(0d));
    }


    @Test
    void testCalculateNewSpeedWithDrag() {
        int currentSpeed = 1;
        int timeSpentSeconds = 1;

        double speed = simulator.calculateNewSpeedWithDrag(currentSpeed, timeSpentSeconds);
        assertThat(speed, equalTo(0d));

        currentSpeed = 1;
        timeSpentSeconds = 9999;
        speed = simulator.calculateNewSpeedWithDrag(currentSpeed, timeSpentSeconds);
        assertThat(speed, equalTo(0d));

        currentSpeed = 10;
        timeSpentSeconds = 2;
        speed = simulator.calculateNewSpeedWithDrag(currentSpeed, timeSpentSeconds);
        assertThat(speed, equalTo(5.8d));
    }

    @Test
    void testCalculateAchievedSpeedsWithNoEffort() {
        Clark clark = new Clark();
        clark.setHorizontal(10);
        clark.setVertical(10);

        //With no effort to move both axes affected by drag
        simulator.calculateAchievedSpeeds(clark, new VoiceCommand(Direction.NORTH, SpeedLevel.L0_RUNNING_HUMAN), 2);
        assertThat(clark.getHorizontal(), equalTo(5.8d));
        assertThat(clark.getVertical(), equalTo(5.8d));

        //No negative impact on speed over long period of time - avoiding overflow
        clark.setHorizontal(1);
        clark.setVertical(1);
        simulator.calculateAchievedSpeeds(clark, new VoiceCommand(Direction.NORTH, SpeedLevel.L0_RUNNING_HUMAN), 99999);
        assertThat(clark.getHorizontal(), equalTo(0d));
        assertThat(clark.getVertical(), equalTo(0d));
    }

    @Test
    void testCalculateAchievedSpeedsMovingVerticalDirection() {
        Clark clark = new Clark();
        clark.setHorizontal(10);
        clark.setVertical(10);
        simulator.calculateAchievedSpeeds(clark, new VoiceCommand(Direction.NORTH, SpeedLevel.L3_SUPER_SONIC), 2);
        assertThat(clark.getHorizontal(), equalTo(5.8d));
        assertThat(clark.getVertical(), equalTo(11.8d));
    }
}
