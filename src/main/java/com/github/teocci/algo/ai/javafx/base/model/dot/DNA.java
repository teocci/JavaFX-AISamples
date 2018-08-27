package com.github.teocci.algo.ai.javafx.base.model.dot;


import static com.github.teocci.algo.ai.javafx.base.utils.Random.uniform;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-23
 */
public class DNA
{
    // Array of vectors which get the dot to the goal (hopefully)
    private Vector2D[] directions;
    private int step = 0;

    public DNA(int size)
    {
        directions = new Vector2D[size];
    }

    /**
     * Sets all the vectors in directions to a random vector
     */
    public void init()
    {
        for (int i = 0; i < directions.length; i++) {
            directions[i] = generateDirection();
        }
    }

    /**
     * Returns a copy of this DNA object
     *
     * @return a DNA object with the copy of directions
     */
    public DNA clone()
    {
        DNA clone = new DNA(directions.length);
        System.arraycopy(directions, 0, clone.directions, 0, directions.length);

        return clone;
    }

    /**
     * Mutates the DNA by setting some of the directions to random vectors
     */
    public void mutate()
    {
        // Probability that any vector in directions mutates
        double mutationRate = 0.01;
        for (int i = 0; i < directions.length; i++) {
            double rand = uniform();
            if (rand < mutationRate) {
                directions[i] = generateDirection();
            }
        }
    }

    /**
     * Returns a random direction
     *
     * @return a vector with a random direction
     */
    private Vector2D generateDirection()
    {
        double angle = uniform(0, 2 * Math.PI);
        return new Vector2D(angle);
    }

    /**
     * Returns a random direction with a random magnitude
     *
     * @return a vector with a random direction
     */
    private Vector2D generateDirection(int maxFactor)
    {
        //set this direction as a random direction
        double angle = uniform(0, 2 * Math.PI);
        double factor = uniform(0, maxFactor);
        Vector2D v = new Vector2D(angle);

        return v.scale(factor);
    }

    public void next()
    {
        step++;
    }

    public int getStep()
    {
        return step;
    }

    public Vector2D[] getDirections()
    {
        return directions;
    }

    public void setDirections(Vector2D[] directions)
    {
        this.directions = directions;
    }
}
