package com.nortal.clark.training.simulator;

import com.nortal.clark.training.assignment.model.CityMap;
import com.nortal.clark.training.assignment.model.Clark;
import com.nortal.clark.training.assignment.model.Direction;
import com.nortal.clark.training.assignment.model.Position;
import com.nortal.clark.training.assignment.model.SpeedLevel;
import com.nortal.clark.training.assignment.model.TrainingResult;
import com.nortal.clark.training.assignment.model.TrainingStatus;
import com.nortal.clark.training.assignment.model.VoiceCommand;
import com.nortal.clark.training.visualizer.TrainingVisualizer;

import java.awt.geom.Line2D;

/**
 * Simulating the world for the training
 */
public class TrainingSimulator {

    /**
     * It takes force equal to 1.6m/s² for Clark to start moving in any direction.
     * Once Clark is moving, drag amount depends on his speed.
     */
    public static final double WATER_DRAG_THRESHOLD = 1.6;

    /**
     * Each training step is executed within 1 second frame
     */
    public static final int FRAME_SIZE_MILLIS = 500;

    /**
     * How close the Clark has to be to the center of target to be considered a successful collection
     */
    public static final int TARGET_PROXIMITY_THRESHOLD = 2;

    /**
     * Training cannot continue indefinitely. There is a timeout of 10 min
     */
    private static final long RACE_TIMEOUT_MILLIS = 10 * 60 * 1000;

    private TrainingResult trainingResult = new TrainingResult();

    private TrainingVisualizer trainingVisualizer;

    public TrainingSimulator() {
        this(true);
    }

    public TrainingSimulator(boolean renderGUI) {
        trainingVisualizer = new TrainingVisualizer(renderGUI);
    }

    public void simulateVoiceReaction(Clark clark, VoiceCommand command, CityMap cityMap) {
        // calculateReachedPosition modifies Clark's position. clone for calculations
        Position positionBeforeCommand = new Position(clark.getPosition());

        double timeSpentSeconds = FRAME_SIZE_MILLIS / 1000.0;
        calculateReachedPosition(clark, command, timeSpentSeconds);
        calculateAchievedSpeeds(clark, command, timeSpentSeconds);

        updateTrainingResult(positionBeforeCommand, clark, cityMap);
        trainingVisualizer.renderVisualizationGUI(clark, command, cityMap);
    }

    TrainingResult updateTrainingResult(Position positionBeforeCommand, Clark clark, CityMap cityMap) {
        if (trainingResult.getTrainingStatus() == null) {
            trainingResult.startTraining();
        }

        trainingResult.addTrainingTime(FRAME_SIZE_MILLIS);

        if (trainingResult.getTrainingTime() > RACE_TIMEOUT_MILLIS) {
            trainingResult.setTrainingStatus(TrainingStatus.TIMEOUT);
            return trainingResult;
        }

        if (!cityMap.isWithinCity(clark.getPosition())) {
            trainingResult.setTrainingStatus(TrainingStatus.OUTSIDE_CITY);
            return trainingResult;
        }

        // Collect any targets on the distance travelled
        captureTargetsOnDistanceTravelled(positionBeforeCommand, clark, cityMap);

        if (isAllTargetsCaptured(cityMap)) {
            trainingResult.setTrainingStatus(TrainingStatus.COMPLETED);
        }

        return trainingResult;
    }

    private boolean isAllTargetsCaptured(CityMap cityMap) {
        // Check if all targets collected
        boolean allTargetsCaptured = true;
        for (Position target : cityMap.getTargets()) {
            if (!trainingResult.getCapturedTargets().contains(target)) {
                allTargetsCaptured = false;
                break;
            }
        }
        return allTargetsCaptured;
    }

    private void captureTargetsOnDistanceTravelled(Position positionBeforeCommand, Clark clark, CityMap cityMap) {
        for (Position target : cityMap.getTargets()) {
            double passingDistance = Line2D.ptSegDist(
                    positionBeforeCommand.x, positionBeforeCommand.y,
                    clark.getPosition().x, clark.getPosition().y,
                    target.x, target.y);

            if (passingDistance < TARGET_PROXIMITY_THRESHOLD) {
                System.err.println("Target captured " + target);
                trainingResult.addCapturedTarget(target);
            }
        }
    }

    private void calculateReachedPosition(Clark clark, VoiceCommand command, double timeSpentSeconds) {

        int displacementX;
        int displacementY;

        Direction direction = command.getDirection();
        SpeedLevel speedLevel = command.getSpeedLevel();

        double speedX = clark.getHorizontal();
        double speedY = clark.getVertical();

        if (speedLevel.equals(SpeedLevel.L0_RUNNING_HUMAN)) {
            //If Clark is not putting any effort, then both directions affected by drag only
            displacementX = calculateDisplacementWithDrag(speedX, timeSpentSeconds);
            displacementY = calculateDisplacementWithDrag(speedY, timeSpentSeconds);

        } else if (Direction.NORTH == direction || Direction.SOUTH == direction) {
            //If Clark is applying force vertically, then X affected by drag, Y by Clark's power
            displacementX = calculateDisplacementWithDrag(speedX, timeSpentSeconds);
            displacementY = calculateDisplacement(speedY, command, timeSpentSeconds);

        } else if (Direction.WEST == direction || Direction.EAST == direction) {
            //If Clark is applying force horizontally, then Y affected by drag, X by Clark's power
            displacementX = calculateDisplacement(speedX, command, timeSpentSeconds);
            displacementY = calculateDisplacementWithDrag(speedY, timeSpentSeconds);

        } else {
            throw new IllegalArgumentException("Voice command " + command + " invalid");
        }

        clark.getPosition().translate(displacementX, displacementY);
    }

    private int calculateDisplacement(double currentSpeed, VoiceCommand command, double timeSpentSeconds) {
        double acceleration = calculateAcceleration(currentSpeed, command.getDirection().getAccelerationModifier(), command.getSpeedLevel());
        return calculateDisplacement(currentSpeed, acceleration, timeSpentSeconds);
    }

    private int calculateDisplacement(double currentSpeed, double acceleration, double timeSpentSeconds) {
        double exactDisplacement = currentSpeed * timeSpentSeconds + 1.0 / 2.0 * acceleration * Math.pow(timeSpentSeconds, 2);
        return (int) Math.round(exactDisplacement);
    }

    /*
     * Drag is a special kind of acceleration that has always an opposite direction to current movement
     * Depending on current movement direction we either increase or decrease the position.
     * We also compensate for overshoot since drag eventually pulls Clark to a stop
     */
    private int calculateDisplacementWithDrag(double currentSpeed, double timeSpentSeconds) {

        double dragAcceleration = getDragAcceleration(currentSpeed);
        int displacement = calculateDisplacement(currentSpeed, dragAcceleration, timeSpentSeconds);

        //compensate overshooting. Drag does not make Clark to change direction
        if (currentSpeed == 0 || (currentSpeed < 0 && displacement > 0) || (currentSpeed > 0 && displacement < 0)) {
            displacement = 0;
        }

        return displacement;
    }

    /**
     * Calculates Clark's speed with the power Clark has put that he achieves in given time (milliseconds)
     */
    void calculateAchievedSpeeds(Clark clark, VoiceCommand command, double timeSpentSeconds) {

        double newSpeedX;
        double newSpeedY;

        double speedX = clark.getHorizontal();
        double speedY = clark.getVertical();

        if (command.getSpeedLevel().equals(SpeedLevel.L0_RUNNING_HUMAN)) {
            //If Clark is not putting any effort, then both directions affected by drag only
            newSpeedX = calculateNewSpeedWithDrag(speedX, timeSpentSeconds);
            newSpeedY = calculateNewSpeedWithDrag(speedY, timeSpentSeconds);

        } else if (Direction.NORTH.equals(command.getDirection()) || Direction.SOUTH.equals(command.getDirection())) {
            //If Clark is applying force vertically, then X affected by drag, Y by Clark's power
            newSpeedX = calculateNewSpeedWithDrag(speedX, timeSpentSeconds);
            newSpeedY = calculateNewSpeed(speedY, command, timeSpentSeconds);

        } else if (Direction.WEST.equals(command.getDirection()) || Direction.EAST.equals(command.getDirection())) {
            //If Clark is applying force horizontally, then Y affected by drag, X by Clark's power
            newSpeedX = calculateNewSpeed(speedX, command, timeSpentSeconds);
            newSpeedY = calculateNewSpeedWithDrag(speedY, timeSpentSeconds);

        } else {
            throw new IllegalArgumentException("Voice command " + command + " invalid");
        }

        //speed = speed + acceleration * timeSpentSeconds;
        clark.setHorizontal(newSpeedX);
        clark.setVertical(newSpeedY);
    }

    /*
     * Drag is a special kind of acceleration that has always an opposite direction to current movement
     * Depending on current movement direction we either increase or decrease the speed.
     * We also compensate for overshoot since the ultimate target speed for drag equals 0
     */
    double calculateNewSpeedWithDrag(double currentSpeed, double timeSpentSeconds) {

        double dragAcceleration = getDragAcceleration(currentSpeed);
        double newSpeed = calculateNewSpeed(currentSpeed, dragAcceleration, timeSpentSeconds);

        //compensate overshooting. Drag does not make Clark to change direction
        if (currentSpeed == 0 || (currentSpeed < 0 && newSpeed > 0) || (currentSpeed > 0 && newSpeed < 0)) {
            newSpeed = 0;
        }

        return newSpeed;
    }

    private double calculateNewSpeed(double currentSpeed, VoiceCommand command, double timeSpentSeconds) {
        double acceleration = calculateAcceleration(currentSpeed, command.getDirection().getAccelerationModifier(), command.getSpeedLevel());
        return calculateNewSpeed(currentSpeed, acceleration, timeSpentSeconds);
    }

    double calculateNewSpeed(double currentSpeed, double acceleration, double timeSpentSeconds) {
        return currentSpeed + acceleration * timeSpentSeconds;
    }

    /**
     * Calculated directional acceleration of Clark and level of speed.
     * For example if Clark is moving EAST with L2_SUB_SONIC then acceleration is positive
     * if Clark is moving WEST with L2_SUB_SONIC then acceleration is negative
     *
     * @param accelerationModifier indicates the direction of acceleration applied at given level.
     *                             The speed and direction are always on the same axis
     */
    double calculateAcceleration(double currentSpeed, int accelerationModifier, SpeedLevel speedLevel) {
        if (accelerationModifier != -1 && accelerationModifier != 1) {
            throw new IllegalArgumentException("Acceleration modifier must be either -1 or 1");
        }

        double acceleration = accelerationModifier * speedLevel.getAcceleration();
        if (currentSpeed == 0) {
            //If current speed for given direction is 0 then soon to apply drag is opposite to acceleration
            //If acceleration does not exceed threshold, Clark does not move
            if (speedLevel.getAcceleration() <= WATER_DRAG_THRESHOLD) {
                return 0;
            }

            double dragAcceleration = -accelerationModifier * WATER_DRAG_THRESHOLD;
            return acceleration + dragAcceleration;
        } else {
            //Otherwise drag is applied depending on the current movement direction
            double dragAcceleration = getDragAcceleration(currentSpeed);
            return acceleration + dragAcceleration;
        }

    }

    /**
     * Calculates directional acceleration of water depending on current speed indicating movement.
     * Drag affects Clark always in the opposite direction to his movement
     * <p>
     * Formula: x = 1.6 + v²/200
     * This gives us vmax = √480 = approximately 21.908902
     */
    double getDragAcceleration(double currentSpeed) {
        // 0 if currentSpeed = 0
        double dragDirectionalModifier = -Math.signum(currentSpeed);
        //
        // Clark's maximum speed is x²
        double waterDrag = WATER_DRAG_THRESHOLD + (Math.pow(currentSpeed, 2) / 200);
        return dragDirectionalModifier * waterDrag;
    }

    public TrainingResult getTrainingResult() {
        return trainingResult;
    }
}
