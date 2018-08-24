package com.github.teocci.algo.ai.javafx.base.model;

import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-23
 */
public class Dot
{
    private static final String TAG = LogHelper.makeLogTag(Dot.class);

    private static final int BASE_INSTRUCTIONS = 1000;
    public static final int BASE_RADIUS = 4;

    private Circle dot;

    private Vector2D pos;
    private Vector2D vel;
    private Vector2D acc;

    private DNA dna;

    private boolean dead = false;
    private boolean reachedGoal = false;
    private boolean best = false; //true if this dot is the best dot from the previous generation

    private double fitness;

    private int width, height;

    public Dot()
    {
        this(0, 0);
    }

    public Dot(int x, int y)
    {
        dna = new DNA(BASE_INSTRUCTIONS);
        dna.init();

        pos = new Vector2D(x, y);
        vel = new Vector2D(0, 0);
        acc = new Vector2D(0, 0);

        dot = new Circle(BASE_RADIUS, Color.BLACK);
        dot.setTranslateX(pos.getX());
        dot.setTranslateY(pos.getY());
    }

    /**
     * Moves the dot if there are still directions left then get the next vector in the directions array
     * and set it as new the acceleration
     */
    public void move()
    {
        Vector2D[] directions = dna.getDirections();
        if (directions == null) throw new NullPointerException("Brain directions is null");

        int step = dna.getStep();
        if (step < 0) throw new RuntimeException("Brain step must be positive");

        if (directions.length > step) {
            acc = directions[step];
            dna.next();
        } else {
            // if is at the end of the directions array then the dot is dead
            die();
        }

        // Apply the acceleration and move the dot
        vel = vel.plus(acc);
        pos = pos.plus(vel);

        dot.translateXProperty().set(pos.getX() > width ? width : pos.getX());
        dot.translateYProperty().set(pos.getY() > height ? height : pos.getY());
    }

    public void update(Dot goal, int width, int height)
    {
        if (!dead && !reachedGoal) {
            setBounds(width, height);
            move();
            verifyCollisions(goal);
//            else if (pos.getX() < 600 && pos.getY() < 310 && pos.getX() > 0 && pos.getY() > 300) {//if hit obstacle
//                dead = true;
//            }
        }
    }

    private void verifyCollisions(Dot goal)
    {
        double radius = getRadius();
        // If near the edges of the window then kill it
        if (pos.getX() < radius || pos.getY() < radius || pos.getX() > width - radius || pos.getY() > height - radius) {
            die();
        } else if (pos.distanceTo(goal.getPos()) < goal.getRadius() - radius) {
            // If reached goal
            gotGoal();
        }
    }

    public void calculateFitness(Vector2D goal)
    {
        if (reachedGoal) {//if the dot reached the goal then the fitness is based on the amount of steps it took to get there
            fitness = 1.0 / 16.0 + 10000.0 / (float) (dna.getStep() * dna.getStep());
        } else {//if the dot didn't reach the goal then the fitness is based on how close it is to the goal
            double distanceToGoal = pos.distanceTo(goal);
            fitness = 1.0 / (distanceToGoal * distanceToGoal);
        }
    }

    /**
     * New dots have the same brain as their parents
     */
    public Dot mitosis()
    {
        Dot newDot = new Dot(width / 2, height);
        newDot.dna = dna.clone();
        return newDot;
    }

    public void die()
    {
        dead = true;
        dot.setFill(Color.RED);
    }

    public void best()
    {
        best = true;
        dot.setFill(Color.LIGHTGREEN);
    }

    public void gotGoal()
    {
        reachedGoal = true;
        dot.setFill(Color.ORANGE);
    }


    public void setBounds(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void setPos(int x, int y)
    {
        pos = new Vector2D(x, y);
        dot.layoutXProperty().set(pos.getX());
        dot.layoutXProperty().set(pos.getY());
    }

    public void setColor(Color color)
    {
        dot.setFill(color);
    }

    public void setRadius(double radius)
    {
        dot.setRadius(radius);
    }


    public Vector2D getPos()
    {
        return pos;
    }

    public void setDot(Circle dot)
    {
        this.dot = dot;
    }

    public Circle getDot()
    {
        return dot;
    }

    private double getRadius()
    {
        return dot.getRadius();
    }

    public DNA getDna()
    {
        return dna;
    }

    public double getFitness()
    {
        return fitness;
    }


    public boolean hasReachedGoal()
    {
        return reachedGoal;
    }

    public boolean isDead()
    {
        return dead;
    }

    public boolean isBest()
    {
        return best;
    }
}
