package com.github.teocci.algo.ai.javafx.base.model.dino.chars;

import com.github.teocci.algo.ai.javafx.base.controllers.dino.MainController;
import com.github.teocci.algo.ai.javafx.base.model.dino.Element;
import com.github.teocci.algo.ai.javafx.base.model.dino.Genome;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

import static com.github.teocci.algo.ai.javafx.base.utils.Config.IMG_BIRD_00;
import static com.github.teocci.algo.ai.javafx.base.utils.Config.IMG_BIRD_01;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-30
 */
public class Bird extends Element
{
    private static final String TAG = LogHelper.makeLogTag(Genome.class);

    private Image[] images = new Image[]{
            new Image(IMG_BIRD_00),
            new Image(IMG_BIRD_01)
    };

    private int w = 60;
    private int h = 50;

    private double posX;
    private double posY;

    private int flapCount = 0;

    private int typeOfBird;

    public Bird(int type)
    {
        posX = MainController.getInstance().getWidth();
        typeOfBird = type;
        switch (type) {
            case 0://flying low
                posY = 10 + h / 2;
                break;
            case 1://flying middle
                posY = 100;
                break;
            case 2://flying high
                posY = 180;
                break;
        }
    }


    /**
     * Moves the bird
     */
    @Override
    public void move(double speed)
    {
        posX -= speed;
    }

    /**
     * Shows the bird
     */
    @Override
    public void show()
    {
        flapCount++;

        int groundHeight = MainController.getInstance().getGroundHeight();
        double height = MainController.getInstance().getHeight();

        // Flap the bird
        if (flapCount < 0) {
//            setFill(loadImage(0));
            updateImage(0);
        } else {
            updateImage(1);
        }

        if (flapCount > 15) {
            flapCount = -15;
        }
    }

    /**
     * Returns whether or not the bird collides with the player
     */
    @Override
    public boolean collided(double playerX, double playerY, double playerWidth, double playerHeight)
    {
        double playerLeft = playerX - playerWidth / 2;
        double playerRight = playerX + playerWidth / 2;
        double thisLeft = posX - w / 2;
        double thisRight = posX + w / 2;

        if ((playerLeft <= thisRight && playerRight >= thisLeft) || (thisLeft <= playerRight && thisRight >= playerLeft)) {
            double playerUp = playerY + playerHeight / 2;
            double playerDown = playerY - playerHeight / 2;
            double thisUp = posY + h / 2;
            double thisDown = posY - h / 2;

            return playerDown <= thisUp && playerUp >= thisDown;
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
        return h;
    }

    private void updateImage(int type)
    {
        Image image = images[type];
        int groundHeight = MainController.getInstance().getGroundHeight();
        double height = MainController.getInstance().getHeight();

        // Draw next image
        GraphicsContext gc = MainController.getInstance().getView().getGc();
        gc.drawImage(image, posX - image.getWidth() / 2, height - groundHeight - (posY + image.getHeight() - 20));
    }

    private Paint loadImage(int type)
    {
        Image image = images[type];
        int groundHeight = MainController.getInstance().getGroundHeight();
        double height = MainController.getInstance().getHeight();
        return new ImagePattern(image, 0, 0, posX - image.getWidth() / 2, height - groundHeight - (posY + image.getHeight() - 20), true);
    }

    public int getTypeOfBird()
    {
        return typeOfBird;
    }
}
