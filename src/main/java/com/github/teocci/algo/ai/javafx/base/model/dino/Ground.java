package com.github.teocci.algo.ai.javafx.base.model.dino;

import com.github.teocci.algo.ai.javafx.base.controllers.dino.MainController;
import com.github.teocci.algo.ai.javafx.base.utils.Random;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class Ground extends Element
{
    private double posX;
    private double posY;

    private int w = Random.uniform(1, 10);

    public Ground() {
        int groundHeight = MainController.getInstance().getGroundHeight();

        posX = getWidth();
        posY = getHeight() - Random.uniform(groundHeight - 20, groundHeight + 30);
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
    }

    @Override
    protected boolean collided(double posX, double v, double v1, double height)
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
