package com.github.teocci.algo.ai.javafx.base.model.dino;

import com.github.teocci.algo.ai.javafx.base.utils.Random;
import javafx.scene.shape.Rectangle;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class Ground extends Rectangle
{
    private double posX = getWidth();
    private double posY = getHeight() - Random.uniform(groundHeight - 20, groundHeight + 30);
    private int w = Random.uniform(1, 10);

    public Ground() {}

    public void show()
    {
//        stroke(0);
//        strokeWeight(3);
//        line(posX, posY, posX + w, posY);
    }

    public void move(float speed)
    {
        posX -= speed;
    }
}
