package com.github.teocci.algo.ai.javafx.base.views.dot;

import com.github.teocci.algo.ai.javafx.base.controllers.dot.GenerationController;
import com.github.teocci.algo.ai.javafx.base.model.dot.Dot;
import com.github.teocci.algo.ai.javafx.base.model.dot.Obstacle;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Sep-13
 */
public class MainView
{
    private final String TAG = LogHelper.makeLogTag(MainView.class);

    private GenerationController controller;

    private BorderPane root = new BorderPane();
    private ToolBar toolBar = new ToolBar();

    //    private Pane canvas = new Pane();
    private Canvas canvas = new Canvas();

    private GraphicsContext gc;

    private Label genValue = new Label("0");
    private Label bestValue = new Label("0");

    public MainView(GenerationController controller, Stage stage)
    {
        this.controller = controller;
        Scene scene = new Scene(root, 800, 960);
        scene.setFill(Color.LIGHTGRAY);

        root.setCenter(canvas);
        root.setBottom(toolBar);

        stage.setTitle("Smart Dots");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        initCanvas();
        initToolBar();
    }

    private void initCanvas()
    {
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty().subtract(toolBar.heightProperty()));

        LogHelper.e("canvas(w, h) -> (" + canvas.getWidth() + ", " + canvas.getHeight() + ')');

        gc = canvas.getGraphicsContext2D();
    }

    private void initToolBar()
    {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        Label genLbl = new Label("Generation:");
        Label bestLbl = new Label("Min steps:");
        toolBar.getItems().setAll(bestLbl, bestValue, region, genLbl, genValue);
    }

    public void drawDots(Dot[] dots)
    {
        for (Dot dot : dots) {
            dot.show(gc);
        }
    }

    public void drawObstacles(Obstacle[] obstacles)
    {
        for (Obstacle obstacle : obstacles) {
            obstacle.show(gc);
        }
    }

    public void drawLine(double x, double y, double endX, double endY)
    {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(x, y, endX, endY);
    }

    public void clear()
    {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    public void setBestValue(int score)
    {
        this.bestValue.setText(String.valueOf(score));
    }

    public void setGenValue(int gen)
    {
        this.genValue.setText(String.valueOf(gen));
    }


    public double getCanvasWidth()
    {
        return canvas.getWidth();
    }

    public double getCanvasHeight()
    {
        return canvas.getHeight();
    }

    public GenerationController getController()
    {
        return controller;
    }

    public GraphicsContext getGc()
    {
        return gc;
    }
}
