package com.nortal.clark.training.test.provided;

import com.nortal.clark.training.assignment.model.CityMap;
import com.nortal.clark.training.assignment.model.Clark;
import com.nortal.clark.training.assignment.model.Direction;
import com.nortal.clark.training.assignment.model.Position;
import com.nortal.clark.training.assignment.model.SpeedLevel;
import com.nortal.clark.training.assignment.model.VoiceCommand;
import com.nortal.clark.training.test.TrainingSimulationTest;
import org.junit.jupiter.api.Test;

class SimpleStraightTest extends TrainingSimulationTest {

    @Test
    void testStraightLineSingleTarget() {
        Clark clark = new Clark();
        CityMap area = new CityMap(new Position(500, 500));
        area.addTarget(200, 200);

        //Initial state
        VoiceCommand voiceCommand = new VoiceCommand(Direction.NORTH, SpeedLevel.L0_RUNNING_HUMAN);
        runSimulation(area, clark, voiceCommand);
    }

    @Test
    void twoTargetsYAxis() {
        Clark clark = new Clark();
        CityMap area = new CityMap(new Position(500, 500));
        area.addTarget(100, 200);
        area.addTarget(100, 20);

        //Initial state
        VoiceCommand voiceCommand = new VoiceCommand(Direction.NORTH, SpeedLevel.L0_RUNNING_HUMAN);
        runSimulation(area, clark, voiceCommand);
    }

    @Test
    void twoTargetsXAxis() {
        Clark clark = new Clark();
        CityMap area = new CityMap(new Position(500, 500));
        area.addTarget(100, 200);
        area.addTarget(150, 200);

        //Initial state
        VoiceCommand voiceCommand = new VoiceCommand(Direction.NORTH, SpeedLevel.L0_RUNNING_HUMAN);
        runSimulation(area, clark, voiceCommand);
    }

    @Test
    void targetsDiagonal() {
        Clark clark = new Clark();
        CityMap area = new CityMap(new Position(500, 500));
        area.addTarget(100, 100);
        area.addTarget(150, 150);
        area.addTarget(250, 250);

        //Initial state
        VoiceCommand voiceCommand = new VoiceCommand(Direction.NORTH, SpeedLevel.L0_RUNNING_HUMAN);
        runSimulation(area, clark, voiceCommand);
    }
}
