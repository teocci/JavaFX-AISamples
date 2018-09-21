package com.github.teocci.algo.ai.javafx.base.model.dot;

import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Sep-21
 */
public class Obstacle
{
    private static final String TAG = LogHelper.makeLogTag(Obstacle.class);

    private Vector2D pos;

    private int width, height;

    private Color color = Color.web("#565656");

    public Obstacle(Vector2D pos, int width, int height)
    {
        this.pos = pos;
        this.width = width;
        this.height = height;
    }

    public boolean collided(Dot dot)
    {
        return dot.getX() < getEndX() && dot.getY() < getEndY() && dot.getX() > pos.getX() && dot.getY() > pos.getY();
    }

    public void show(GraphicsContext gc)
    {
        gc.setFill(color);
        gc.fillRect(pos.getX(), pos.getY(), width, height);
    }


    public void setColor(Color color)
    {
        this.color = color;
    }


    public Vector2D getPos()
    {
        return pos;
    }

    public Color getColor()
    {
        return color;
    }

    private double getEndX()
    {
        return pos.getX() + width;
    }

    private double getEndY()
    {
        return pos.getY() + height;
    }
}
