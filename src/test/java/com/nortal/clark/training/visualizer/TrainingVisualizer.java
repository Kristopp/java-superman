package com.nortal.clark.training.visualizer;

import com.nortal.clark.training.assignment.model.CityMap;
import com.nortal.clark.training.assignment.model.Clark;
import com.nortal.clark.training.assignment.model.Direction;
import com.nortal.clark.training.assignment.model.Position;
import com.nortal.clark.training.assignment.model.SpeedLevel;
import com.nortal.clark.training.assignment.model.VoiceCommand;
import com.nortal.clark.training.simulator.TrainingSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class TrainingVisualizer {

    public static final int VISUALIZATION_PAUSE_MILLIS = 10;

    private static final int PADDING = 12;
    private static final int CLARK_POSITION_RADIUS = 10;
    private static final int TARGET_RADIUS = 10;

    private JFrame frame;
    private JPanel pane;
    private VoiceCommand voiceCommand;

    private Clark clark;
    private CityMap cityMap;

    // true if GUI needs to be initialized. Set to false once the GUI has been created
    private boolean createGUI = false;

    public TrainingVisualizer(boolean createGUI) {
        this.createGUI = createGUI;

        String disableGuiProperty = System.getProperty("simulator.disable.gui");
        Boolean disableGui = Boolean.valueOf(disableGuiProperty);
        if (Boolean.TRUE.equals(disableGui)) {
            //For automation
            System.out.println("GUI is disabled");
            this.createGUI = false;
        }

    }

    public void renderVisualizationGUI(final Clark clark, final VoiceCommand voiceCommand, final CityMap cityMap) {

        if (createGUI) {

            //Always update values with latest data
            this.cityMap = cityMap;
            this.clark = clark;
            this.voiceCommand = voiceCommand;

            try {
                //Slow down the calculations to be able to see what's happening
                Thread.sleep(VISUALIZATION_PAUSE_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (frame == null) {
                createGui();
            }
            frame.repaint();
        }
    }

    private void createGui() {
        frame = new JFrame(getClass().getSimpleName());

        pane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); //ALWAYS call this method first!

                Graphics2D g2d = (Graphics2D) g;
                g2d.setBackground(Color.WHITE);

                if (cityMap != null) {
                    paintCityMap(g2d);
                }

                if (clark != null) {
                    paintClark(g2d);
                }

                if (voiceCommand != null) {
                    paintCape(g2d);
                }

                g2d.dispose();
                g.dispose();
            }

            private void paintCityMap(Graphics2D g2d) {
                double vizAcceleration = TrainingSimulator.FRAME_SIZE_MILLIS / VISUALIZATION_PAUSE_MILLIS;
                g2d.drawString(vizAcceleration + " x speed", 11, 11);
                g2d.fill(new Rectangle2D.Double(PADDING, PADDING, cityMap.getWidth(), cityMap.getHeight()));

                g2d.setColor(Color.GREEN);
                for (Position target : cityMap.getTargets()) {
                    g2d.fill(new Rectangle2D.Double(getX(target), getY(target), TARGET_RADIUS, TARGET_RADIUS));
                }
                g2d.setColor(Color.BLACK);
            }

            private void paintCape(Graphics2D g2d) {
                Position capeStartPosition = new Position(clark.getPosition());
                capeStartPosition.translate(5, -5);
                Direction direction = voiceCommand.getDirection();
                SpeedLevel speedLevel = voiceCommand.getSpeedLevel();
                int capeLength = (int) Math.round(speedLevel.getAcceleration() * 2);

                Position capeEndPosA = new Position(capeStartPosition);
                Position capeEndPosB = new Position(capeStartPosition);

                int capeEndPosDelta = -direction.getAccelerationModifier() * (5 + capeLength);

                if (Direction.NORTH.equals(direction) || Direction.SOUTH.equals(direction)) {
                    capeEndPosA.translate(-2, capeEndPosDelta);
                    capeEndPosB.translate(2, capeEndPosDelta);
                } else if (Direction.WEST.equals(direction) || Direction.EAST.equals(direction)) {
                    capeEndPosA.translate(capeEndPosDelta, -2);
                    capeEndPosB.translate(capeEndPosDelta, 2);
                }

                if (!SpeedLevel.L0_RUNNING_HUMAN.equals(speedLevel)) {
                    Stroke prevStroke = g2d.getStroke();
                    g2d.setStroke(new BasicStroke(2));
                    g2d.setColor(Color.RED);
                    g2d.draw(new Line2D.Double(getPositionAsPoint(capeStartPosition), getPositionAsPoint(capeEndPosA)));
                    g2d.draw(new Line2D.Double(getPositionAsPoint(capeStartPosition), getPositionAsPoint(capeEndPosB)));
                    g2d.draw(new Line2D.Double(getPositionAsPoint(capeEndPosA), getPositionAsPoint(capeEndPosB)));
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(prevStroke);
                }
            }

            private void paintClark(Graphics2D g2d) {
                g2d.setColor(Color.ORANGE);
                g2d.fill(new Ellipse2D.Double(getX(clark.getPosition()), getY(clark.getPosition()), CLARK_POSITION_RADIUS, CLARK_POSITION_RADIUS));
            }

            private Point getPositionAsPoint(Position position) {
                //X axis is shifted in simulator
                //Y axis is flipped in simulator
                return new Point(getX(position), getY(position));
            }

            private int getX(Position position) {
                //X axis is shifted in simulator
                return position.x + PADDING;
            }

            private int getY(Position position) {
                //Y axis is flipped in simulator
                return cityMap.getHeight() - position.y;
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500 + 2 * PADDING, 500 + 2 * PADDING);
            }
        };

        frame.add(pane);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
