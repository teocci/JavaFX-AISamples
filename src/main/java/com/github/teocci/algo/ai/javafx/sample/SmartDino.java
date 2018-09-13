package com.github.teocci.algo.ai.javafx.sample;

import com.github.teocci.algo.ai.javafx.base.animators.dino.DinoAnimator;
import com.github.teocci.algo.ai.javafx.base.animators.dot.DotAnimator;
import com.github.teocci.algo.ai.javafx.base.controllers.dino.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class SmartDino extends Application
{
    private DinoAnimator animator;

    @Override
    public void start(Stage stage)
    {
        MainController controller = MainController.getInstance(stage);
        controller.setup();
//        generationController = new GenerationController(1000, canvas, genValue, bestValue);

        animator = new DinoAnimator(controller);
        animator.start();
    }

    public static void main(String[] args)
    {
        launch();
    }
}
