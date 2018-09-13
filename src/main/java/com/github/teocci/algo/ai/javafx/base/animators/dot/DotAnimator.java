package com.github.teocci.algo.ai.javafx.base.animators.dot;

import com.github.teocci.algo.ai.javafx.base.animators.Animator;
import com.github.teocci.algo.ai.javafx.base.controllers.dot.GenerationController;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-23
 */
public class DotAnimator extends Animator<GenerationController>
{
    private static final String TAG = LogHelper.makeLogTag(DotAnimator.class);

    public DotAnimator(GenerationController controller)
    {
        super(controller);
    }

    @Override
    public void handle(long now)
    {
        // max speed: 100 hundred times per second
        if (now - lastTime > 10000000) {
            lastTime = now;

            if (controller.allDotsDead()) {
                // Genetic algorithm
                controller.calculateFitness();
                controller.naturalSelection();
                controller.mutate();
            } else {
                // If any of the dots are still alive then update and then show them
//                Platform.runLater(() -> {
                controller.update();
//                });
            }
        }
    }
}
