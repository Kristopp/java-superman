package com.nortal.clark.training.simulator;

import com.nortal.clark.training.assignment.model.Direction;
import com.nortal.clark.training.assignment.model.SpeedLevel;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class TrainingSimulatorAccelerationCalculationTest {

    private TrainingSimulator simulator = new TrainingSimulator(false);

    @Test
    void testDragAccelerationDirection() {
        double dragAcceleration = simulator.getDragAcceleration(-1);
        assertThat(dragAcceleration > 0, equalTo(true));

        dragAcceleration = simulator.getDragAcceleration(1);
        assertThat(dragAcceleration < 0, equalTo(true));

        dragAcceleration = simulator.getDragAcceleration(0);
        double roundedAcceleration = Math.round(dragAcceleration * 100) / 100;
        assertThat(roundedAcceleration, equalTo(0d));
    }

    @Test
    void testCalculateAccelerationWith0SpeedAndLowEffort() {
        double acceleration = simulator.calculateAcceleration(0, -1, SpeedLevel.L1_TRAIN);
        assertThat(acceleration, equalTo(0d));

        acceleration = simulator.calculateAcceleration(0, 1, SpeedLevel.L1_TRAIN);
        assertThat(acceleration, equalTo(0d));
    }

    @Test
    void testCalculateAccelerationWith0Speed() {
        double acceleration = simulator.calculateAcceleration(0, 1, SpeedLevel.L2_SUB_SONIC);
        double t2LaunchAcceleration = SpeedLevel.L2_SUB_SONIC.getAcceleration() - 1.6;
        assertThat(acceleration, equalTo(t2LaunchAcceleration));

        acceleration = simulator.calculateAcceleration(0, -1, SpeedLevel.L2_SUB_SONIC);
        assertThat(acceleration, equalTo(-t2LaunchAcceleration));
    }

    @Test
    void testCalculateAccelerationX() {
        double
        acceleration = simulator.calculateAcceleration(1, Direction.EAST.getAccelerationModifier(), SpeedLevel.L2_SUB_SONIC);
        assertThat(acceleration, equalTo(0.395d));

        acceleration = simulator.calculateAcceleration(1, Direction.WEST.getAccelerationModifier(), SpeedLevel.L2_SUB_SONIC);
        assertThat(acceleration, equalTo(-3.605d));

        acceleration = simulator.calculateAcceleration(10, Direction.WEST.getAccelerationModifier(), SpeedLevel.L3_SUPER_SONIC);
        assertThat(acceleration, equalTo(-5.1d));

        acceleration = simulator.calculateAcceleration(21.9, Direction.EAST.getAccelerationModifier(), SpeedLevel.L4_MACH_9350);
        //Round to 2 decimal places
        double roundedAcceleration = Math.round(acceleration * 100) / 100;
        assertThat(roundedAcceleration, equalTo(0d));
    }

    @Test
    void testCalculateAccelerationY() {
        double acceleration = simulator.calculateAcceleration(1, Direction.NORTH.getAccelerationModifier(), SpeedLevel.L2_SUB_SONIC);
        assertThat(acceleration, equalTo(0.395d));

        acceleration = simulator.calculateAcceleration(1, Direction.SOUTH.getAccelerationModifier(), SpeedLevel.L2_SUB_SONIC);
        assertThat(acceleration, equalTo(-3.605d));

        acceleration = simulator.calculateAcceleration(10, Direction.SOUTH.getAccelerationModifier(), SpeedLevel.L3_SUPER_SONIC);
        assertThat(acceleration, equalTo(-5.1d));

        acceleration = simulator.calculateAcceleration(21.9, Direction.NORTH.getAccelerationModifier(), SpeedLevel.L4_MACH_9350);
        //Round to 2 decimal places
        double roundedAcceleration = Math.round(acceleration * 100) / 100;
        assertThat(roundedAcceleration, equalTo(0d));
    }
}
