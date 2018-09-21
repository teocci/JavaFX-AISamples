package com.github.teocci.algo.ai.javafx.base.model.dot;

import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import javafx.scene.canvas.GraphicsContext;
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

//    private Circle dot;

    private Vector2D pos;
    private Vector2D vel;
    private Vector2D acc;

    private DNA dna;

    private Color color = Color.BLACK;

    private boolean dead = false;
    private boolean reachedGoal = false;
    private boolean best = false; //true if this dot is the best dot from the previous generation

    private double fitness;

    private int width, height;
    private double radius;

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

        radius = BASE_RADIUS;
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
    }

    public void update(Dot goal, Obstacle[] obstacles, int width, int height)
    {
        if (!dead && !reachedGoal) {
            setBounds(width, height);
            move();
            collisions(goal, obstacles);
        }
    }

    private void collisions(Dot goal, Obstacle[] obstacles)
    {
        // If near the edges of the window then kill it
        if (checkIfDead() || checkIfObstacle(obstacles)) {
            die();
        } else if (checkIfGoal(goal)) {
            // If reached goal
            gotGoal();
        }
    }

    private boolean checkIfDead()
    {
        double radius = getRadius();
        return pos.getX() < radius || pos.getY() < radius || pos.getX() > width - radius || pos.getY() > height - radius;
    }

    private boolean checkIfObstacle(Obstacle[] obstacles)
    {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.collided(this)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkIfGoal(Dot goal)
    {
        double radius = getRadius();
        return pos.distanceTo(goal.getPos()) < goal.getRadius() - radius;
    }

    public void show(GraphicsContext gc)
    {
        gc.setFill(color);
        gc.fillOval(pos.getX(), pos.getY(), radius * 2, radius * 2);
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
        Dot newDot = new Dot(width / 2, height - BASE_RADIUS);
        newDot.dna = dna.clone();
        return newDot;
    }

    public void die()
    {
        dead = true;
        color = Color.ORANGERED;
    }

    public void best()
    {
        best = true;
        color = Color.LIGHTGREEN;
    }

    public void gotGoal()
    {
        reachedGoal = true;
        color = Color.MAGENTA;
    }


    public void setBounds(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void setPos(int x, int y)
    {
        pos = new Vector2D(x, y);
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public void setRadius(double radius)
    {
        this.radius = radius;
    }


    public Vector2D getPos()
    {
        return pos;
    }

    public double getX()
    {
        return pos.getX();
    }

    public double getY()
    {
        return pos.getY();
    }

    public double getRadius()
    {
        return radius;
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
