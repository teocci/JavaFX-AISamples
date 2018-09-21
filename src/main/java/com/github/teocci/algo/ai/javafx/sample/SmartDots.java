package com.github.teocci.algo.ai.javafx.sample;

import com.github.teocci.algo.ai.javafx.base.animators.dot.DotAnimator;
import com.github.teocci.algo.ai.javafx.base.controllers.dot.GenerationController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-23
 */
public class SmartDots extends Application
{
    private DotAnimator dotAnimator;

    @Override
    public void start(Stage stage)
    {
        GenerationController generationController = new GenerationController(stage);

        dotAnimator = new DotAnimator(generationController);
        dotAnimator.start();
    }

    public static void main(String[] args)
    {
        launch();
    }
}
