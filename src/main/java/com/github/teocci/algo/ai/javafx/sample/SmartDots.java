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
    private GenerationController generationController;

    private DotAnimator dotAnimator;

    @Override
    public void start(Stage stage)
    {
        Pane canvas = new Pane();
        Scene scene = new Scene(canvas, 800, 800);

        generationController = new GenerationController(1000, canvas);

        Circle ball = new Circle(10, Color.BLACK);
        ball.relocate(0, 10);

        canvas.getChildren().addAll(generationController.getDots());

        stage.setTitle("Smart Dots");
        stage.setScene(scene);
        stage.show();


        dotAnimator = new DotAnimator(generationController);
        dotAnimator.start();

//        Bounds bounds = canvas.getBoundsInLocal();
//        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3),
//                new KeyValue(ball.layoutXProperty(), bounds.getMaxX() - ball.getRadius()))
//        );
//        timeline.setCycleCount(2);
//        timeline.play();

//        while (true) {
//            if (simulator.allDotsDead()) {
//                //genetic algorithm
//                simulator.calculateFitness();
//                simulator.naturalSelection();
//                simulator.mutate();
//            } else {
//                //if any of the dots are still alive then update and then show them
//                Platform.runLater(() -> {
//                    simulator.update();
//                    canvas.getChildren().clear();
//                    canvas.getChildren().addAll(simulator.getDots());
//
//                });
//            }
//        }

//        Bounds bounds = canvas.getBoundsInLocal();
//        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3),
//                new KeyValue(ball.layoutXProperty(), bounds.getMaxX() - ball.getRadius())));
//        timeline.setCycleCount(2);
//        timeline.play();
    }

    public static void main(String[] args)
    {
        launch();
    }
}
