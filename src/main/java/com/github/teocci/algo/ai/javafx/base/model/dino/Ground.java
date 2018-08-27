package com.github.teocci.algo.ai.javafx.base.model.dino;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class Ground
{
    private float posX = width;
    private float posY = height - floor(random(groundHeight - 20, groundHeight + 30));
    private int w = floor(random(1, 10));

    public Ground() {}

    public void show()
    {
        stroke(0);
        strokeWeight(3);
        line(posX, posY, posX + w, posY);

    }

    public void move(float speed)
    {
        posX -= speed;
    }
}
