package com.github.teocci.algo.ai.javafx.base.model.dino;

import com.github.teocci.algo.ai.javafx.base.model.dot.Dot;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class Obstacle
{
    private static final String TAG = LogHelper.makeLogTag(Obstacle.class);

    private static final int TYPE_SMALL = 0;
    private static final int TYPE_BIG = 1;
    private static final int TYPE_MANY = 2;


    private float posX;
    private int w;
    private int h;
    private int type;

    public Obstacle(int t)
    {
        posX = width;
        type = t;
        switch (type) {
            case TYPE_SMALL:
                w = 40;
                h = 80;
                break;
            case TYPE_BIG:
                w = 60;
                h = 120;
                break;
            case TYPE_MANY:
                w = 120;
                h = 80;
                break;
        }
    }

    //show the cactus
    public void show()
    {
        fill(0);
        rectMode(CENTER);
        switch (type) {
            case 0:
                image(smallCactus, posX - smallCactus.width / 2, height - groundHeight - smallCactus.height);
                break;
            case 1:
                image(bigCactus, posX - bigCactus.width / 2, height - groundHeight - bigCactus.height);
                break;
            case 2:
                image(manySmallCactus, posX - manySmallCactus.width / 2, height - groundHeight - manySmallCactus.height);
                break;
        }
    }

    // move the obstacle
    public void move(float speed)
    {
        posX -= speed;
    }

    //returns whether or not the player collides with this obstacle
    public boolean collided(float playerX, float playerY, float playerWidth, float playerHeight)
    {
        float playerLeft = playerX - playerWidth / 2;
        float playerRight = playerX + playerWidth / 2;
        float thisLeft = posX - w / 2;
        float thisRight = posX + w / 2;

        if ((playerLeft <= thisRight && playerRight >= thisLeft) || (thisLeft <= playerRight && thisRight >= playerLeft)) {
            float playerDown = playerY - playerHeight / 2;
            float thisUp = h;
            if (playerDown <= thisUp) {
                return true;
            }
        }
        return false;
    }
}
