package com.github.teocci.algo.ai.javafx.sample;

import com.github.teocci.algo.ai.javafx.base.animators.dot.DotAnimator;
import com.github.teocci.algo.ai.javafx.base.controllers.dot.GenerationController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-24
 */
public class AISmartDots extends Application
{
    private GenerationController generationController;

    private DotAnimator dotAnimator;

    @Override
    public void start(Stage stage)
    {
        BorderPane root = new BorderPane();
        ToolBar toolBar = new ToolBar();
        Pane canvas = new Pane();
        Scene scene = new Scene(root, 800, 960);
        scene.setFill(Color.LIGHTGRAY);


        root.setCenter(canvas);
        root.setBottom(toolBar);

        stage.setTitle("Smart Dots");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Label genLbl = new Label("Generation:");
        Label genValue = new Label("0");
        Label bestLbl = new Label("Min steps:");
        Label bestValue = new Label("10000");

        generationController = new GenerationController(1000, canvas, genValue, bestValue);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        toolBar.getItems().setAll(genLbl, genValue, region, bestLbl, bestValue);

        dotAnimator = new DotAnimator(generationController);
        dotAnimator.start();
    }

    public static void main(String[] args)
    {
        launch();
    }
}
