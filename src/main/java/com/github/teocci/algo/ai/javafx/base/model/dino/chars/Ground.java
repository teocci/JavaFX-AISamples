package com.github.teocci.algo.ai.javafx.base.model.dino.chars;

import com.github.teocci.algo.ai.javafx.base.controllers.dino.MainController;
import com.github.teocci.algo.ai.javafx.base.model.dino.Element;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import com.github.teocci.algo.ai.javafx.base.utils.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class Ground extends Element
{
    private static final String TAG = LogHelper.makeLogTag(Ground.class);

    private double posX;
    private double posY;

    private int w = Random.uniform(1, 10);

    public Ground()
    {
        int groundHeight = MainController.getInstance().getGroundHeight();

        posX = MainController.getInstance().getWidth();
        posY = MainController.getInstance().getHeight() - Random.uniform(groundHeight - 20, groundHeight + 30);
    }

    @Override
    public void move(double speed)
    {
        posX -= speed;
    }

    @Override
    public void show()
    {
//        stroke(0);
//        strokeWeight(3);
//        line(posX, posY, posX + w, posY);
//        Platform.runLater(() -> MainController.getInstance().getView().drawLine(posX, posY, posX + w, posY));

        LogHelper.e(TAG, "(posX, posY, posX + w) -> (" + posX + ", " + posY + ", " + posX + w + ')');

        GraphicsContext gc = MainController.getInstance().getView().getGc();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(posX, posY, posX + w, posY);
    }

    @Override
    public boolean collided(double posX, double v, double v1, double height)
    {
        return false;
    }

    @Override
    public double getPosX()
    {
        return posX;
    }

    @Override
    public double getPosY()
    {
        return posY;
    }

    @Override
    public int getW()
    {
        return w;
    }

    @Override
    public int getH()
    {
        return -1;
    }
}
