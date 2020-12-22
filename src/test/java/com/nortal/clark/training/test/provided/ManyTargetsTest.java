package com.nortal.clark.training.test.provided;

import com.nortal.clark.training.assignment.model.CityMap;
import com.nortal.clark.training.assignment.model.Clark;
import com.nortal.clark.training.assignment.model.Direction;
import com.nortal.clark.training.assignment.model.Position;
import com.nortal.clark.training.assignment.model.SpeedLevel;
import com.nortal.clark.training.assignment.model.VoiceCommand;
import com.nortal.clark.training.test.TrainingSimulationTest;
import org.junit.jupiter.api.Test;

class ManyTargetsTest extends TrainingSimulationTest {

    @Test
    void manyTargets() {
        Clark clark = new Clark();
        CityMap area = new CityMap(new Position(500, 500));
        area.addTarget(100, 200);
        area.addTarget(150, 120);
        area.addTarget(350, 300);
        area.addTarget(370, 240);
        area.addTarget(100, 370);
        area.addTarget(400, 100);

        //Initial state
        VoiceCommand voiceCommand = new VoiceCommand(Direction.NORTH, SpeedLevel.L0_RUNNING_HUMAN);
        runSimulation(area, clark, voiceCommand);
    }
}
