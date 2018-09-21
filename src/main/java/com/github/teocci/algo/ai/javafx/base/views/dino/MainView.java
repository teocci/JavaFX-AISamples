package com.github.teocci.algo.ai.javafx.base.views.dino;

import com.github.teocci.algo.ai.javafx.base.controllers.dino.MainController;
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
import javafx.stage.Stage;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Sep-13
 */
public class MainView
{
    private final String TAG = LogHelper.makeLogTag(MainView.class);

    private MainController controller;

    private BorderPane root = new BorderPane();
    private ToolBar toolBar = new ToolBar();

//    private Pane canvas = new Pane();

    private Canvas canvas = new Canvas();

    private GraphicsContext gc;

    private Label scoreValue = new Label("0");
    private Label genValue = new Label("0");

    public MainView(MainController controller, Stage stage)
    {
        this.controller = controller;
        Scene scene = new Scene(root, 1280, 720);
        scene.setFill(Color.LIGHTGRAY);

        root.setCenter(canvas);
        root.setBottom(toolBar);

        stage.setTitle("Smart Dino");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        Label scoreLbl = new Label("Score:");
        Label genLbl = new Label("Generation:");

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        toolBar.getItems().setAll(scoreLbl, scoreValue, region, genLbl, genValue);

        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty().subtract(toolBar.heightProperty()));

        LogHelper.e("canvas(w, h) -> (" + canvas.getWidth() + ", " + canvas.getHeight() + ')');

        gc = canvas.getGraphicsContext2D();
    }

    public void drawLine(double x, double y, double endX, double endY)
    {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeLine(x, y, endX, endY);
    }

    public void clear()
    {
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
    }


    public void setScoreValue(int score)
    {
        this.scoreValue.setText(String.valueOf(score));
    }

    public void setGenValue(int gen)
    {
        this.genValue.setText(String.valueOf(gen));
    }


    public MainController getController()
    {
        return controller;
    }

    public GraphicsContext getGc()
    {
        return gc;
    }
}
