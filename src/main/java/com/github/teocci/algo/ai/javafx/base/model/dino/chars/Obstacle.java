package com.github.teocci.algo.ai.javafx.base.model.dino.chars;

import com.github.teocci.algo.ai.javafx.base.controllers.dino.MainController;
import com.github.teocci.algo.ai.javafx.base.model.dino.Element;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

import static com.github.teocci.algo.ai.javafx.base.utils.Config.IMG_CACTUS_BIG;
import static com.github.teocci.algo.ai.javafx.base.utils.Config.IMG_CACTUS_MANY;
import static com.github.teocci.algo.ai.javafx.base.utils.Config.IMG_CACTUS_SMALL;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class Obstacle extends Element
{
    private static final String TAG = LogHelper.makeLogTag(Obstacle.class);

    private Image[] images = new Image[]{
            new Image(IMG_CACTUS_SMALL),
            new Image(IMG_CACTUS_BIG),
            new Image(IMG_CACTUS_MANY)
    };

    private static final int TYPE_SMALL = 0;
    private static final int TYPE_BIG = 1;
    private static final int TYPE_MANY = 2;

    private double posX;

    private int w;
    private int h;

    private int type;

    public Obstacle(int t)
    {
        posX = MainController.getInstance().getWidth();
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

    // move the obstacle
    @Override
    public void move(double speed)
    {
        posX -= speed;
    }

    @Override
    public void show()
    {
//        fill(0);
//        rectMode(CENTER);
//        setFill(loadImage(type));
        updateImage(type);
    }

    /**
     * Returns whether or not the player collides with this obstacle
     */
    @Override
    public boolean collided(double playerX, double playerY, double playerWidth, double playerHeight)
    {
        double playerLeft = playerX - playerWidth / 2;
        double playerRight = playerX + playerWidth / 2;
        double thisLeft = posX - w / 2;
        double thisRight = posX + w / 2;

        if ((playerLeft <= thisRight && playerRight >= thisLeft) || (thisLeft <= playerRight && thisRight >= playerLeft)) {
            double playerDown = playerY - playerHeight / 2;
            double thisUp = h;
            return playerDown <= thisUp;
        }
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
        return -1;
    }

    @Override
    public int getW()
    {
        return w;
    }

    @Override
    public int getH()
    {
        return h;
    }

    private void updateImage(int type)
    {
        Image image = images[type];
        int groundHeight = MainController.getInstance().getGroundHeight();
        double height = MainController.getInstance().getHeight();

        // Draw next image
        GraphicsContext gc = MainController.getInstance().getView().getGc();
        gc.drawImage(image, posX - image.getWidth() / 2, height - groundHeight - image.getHeight());
    }

    private Paint loadImage(int type)
    {
        Image image = images[type];
        int groundHeight = MainController.getInstance().getGroundHeight();
        double height = MainController.getInstance().getHeight();

        return new ImagePattern(image, 0, 0, posX - image.getWidth() / 2, height - groundHeight - image.getHeight(), true);
    }
}
