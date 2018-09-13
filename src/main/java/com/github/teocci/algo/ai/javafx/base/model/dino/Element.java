package com.github.teocci.algo.ai.javafx.base.model.dino;

import javafx.scene.shape.Rectangle;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-31
 */
public abstract class Element extends Rectangle
{
    public abstract void move(double speed);

    public abstract void show();

    public abstract boolean collided(double posX, double v, double v1, double height);

    public abstract double getPosX();
    public abstract double getPosY();

    public abstract int getW();
    public abstract int getH();
}
