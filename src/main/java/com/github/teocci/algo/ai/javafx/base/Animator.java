package com.github.teocci.algo.ai.javafx.base;

import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-23
 */
public class Animator extends AnimationTimer
{
    private static final String TAG = LogHelper.makeLogTag(Animator.class);

    private long lastTime = System.currentTimeMillis();

    private Simulator simulator;
    private Label genValue, bestValue;

    public Animator(Simulator simulator)
    {
        this.simulator = simulator;
    }

    @Override
    public void handle(long now)
    {
        // max speed: 100 hundred times per second
        if (now - lastTime > 10000000) {
            lastTime = now;
            if (simulator.allDotsDead()) {
                // Genetic algorithm
                simulator.calculateFitness();
                simulator.naturalSelection();
                simulator.mutate();
            } else {
                // If any of the dots are still alive then update and then show them
//                Platform.runLater(() -> {
                simulator.update();
//                });
            }
        }
    }
}
