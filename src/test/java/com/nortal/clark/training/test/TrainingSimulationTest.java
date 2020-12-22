package com.nortal.clark.training.test;

import com.esotericsoftware.kryo.Kryo;
import com.nortal.clark.training.assignment.controller.You;
import com.nortal.clark.training.assignment.model.CityMap;
import com.nortal.clark.training.assignment.model.Clark;
import com.nortal.clark.training.assignment.model.TrainingResult;
import com.nortal.clark.training.assignment.model.TrainingStatus;
import com.nortal.clark.training.assignment.model.VoiceCommand;
import com.nortal.clark.training.simulator.TrainingSimulator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class TrainingSimulationTest {

    protected TrainingResult runSimulation(CityMap area, Clark clark, VoiceCommand voiceCommand) {
        You controller = new You();
        TrainingSimulator simulator = new TrainingSimulator(true);

        Kryo kryo = new Kryo();

        TrainingResult trainingResult = simulator.getTrainingResult();
        while (!trainingResult.isTrainingOver()) {
            simulator.simulateVoiceReaction(clark, voiceCommand, area);

            Clark clarkCopy = kryo.copy(clark);
            CityMap areaCopy = kryo.copy(area);
            //Pass copies to controller to avoid accidental modification of "real life"
            voiceCommand = controller.getNextStep(clarkCopy, areaCopy);
        }

        System.out.println("Training is over in " + trainingResult.getTrainingTime() / 1000 + "s");
        System.out.println("Training result " + trainingResult.getTrainingStatus());

        assertThat(trainingResult.isTrainingOver(), equalTo(true));
        assertThat(trainingResult.getTrainingStatus(), equalTo(TrainingStatus.COMPLETED));

        return trainingResult;
    }
}
