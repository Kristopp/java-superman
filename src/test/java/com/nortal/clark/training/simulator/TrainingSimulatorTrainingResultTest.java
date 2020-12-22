package com.nortal.clark.training.simulator;

import com.nortal.clark.training.assignment.model.CityMap;
import com.nortal.clark.training.assignment.model.Clark;
import com.nortal.clark.training.assignment.model.Position;
import com.nortal.clark.training.assignment.model.TrainingResult;
import com.nortal.clark.training.assignment.model.TrainingStatus;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

class TrainingSimulatorTrainingResultTest {

    private TrainingSimulator simulator = new TrainingSimulator();

    private Position startingPosition = new Position(1, 1);

    @Test
    void testVoiceCrashed() {
        Clark clark = new Clark();
        clark.setPosition(new Position(11, 9));
        CityMap area = new CityMap(new Position(10, 10));
        area.addTarget(1, 1);

        TrainingResult trainingResult = simulator.updateTrainingResult(startingPosition, clark, area);
        assertThat(trainingResult.isTrainingOver(), equalTo(true));
        assertThat(trainingResult.getTrainingStatus(), equalTo(TrainingStatus.OUTSIDE_CITY));

        clark.setPosition(new Position(10, 9));
        trainingResult = simulator.updateTrainingResult(startingPosition, clark, area);
        assertThat(trainingResult.isTrainingOver(), equalTo(true));
        assertThat(trainingResult.getTrainingStatus(), equalTo(TrainingStatus.OUTSIDE_CITY));

        clark.setPosition(new Position(9, 10));
        trainingResult = simulator.updateTrainingResult(startingPosition, clark, area);
        assertThat(trainingResult.isTrainingOver(), equalTo(true));
        assertThat(trainingResult.getTrainingStatus(), equalTo(TrainingStatus.OUTSIDE_CITY));
    }

    @Test
    void testRaceTimedOut() {
        Clark clark = new Clark();
        CityMap area = new CityMap(new Position(10, 10));
        area.addTarget(9, 2);
        simulator.getTrainingResult().startTraining();
        simulator.getTrainingResult().addTrainingTime(11 * 60 * 1000);
        TrainingResult trainingResult = simulator.updateTrainingResult(startingPosition, clark, area);
        assertThat(trainingResult.isTrainingOver(), equalTo(true));
        assertThat(trainingResult.getTrainingStatus(), equalTo(TrainingStatus.TIMEOUT));
    }

    @Test
    void testAllPositionsCaptured() {
        Clark clark = new Clark();
        CityMap area = new CityMap(new Position(10, 10));
        area.addTarget(1, 2);
        area.addTarget(1, 6);

        //Moving from 1,1 to 1,2 - exactly on target
        clark.setPosition(new Position(1, 2));
        TrainingResult trainingResult = simulator.updateTrainingResult(new Position(1, 1), clark, area);
        assertThat(trainingResult.isTrainingOver(), equalTo(false));
        assertThat(trainingResult.getCapturedTargets(), notNullValue());
        assertThat(trainingResult.getCapturedTargets(), hasSize(1));

        //Moving from 1,2 to 1,8 - over the target within threshold
        clark.setPosition(new Position(1, 8));
        trainingResult = simulator.updateTrainingResult(new Position(1, 2), clark, area);
        assertThat(trainingResult.getCapturedTargets(), hasSize(2));
        assertThat(trainingResult.isTrainingOver(), equalTo(true));
        assertThat(trainingResult.getTrainingStatus(), equalTo(TrainingStatus.COMPLETED));
    }
}
