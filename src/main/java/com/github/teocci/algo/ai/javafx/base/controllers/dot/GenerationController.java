package com.github.teocci.algo.ai.javafx.base.controllers.dot;

import com.github.teocci.algo.ai.javafx.base.model.dot.Dot;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import com.github.teocci.algo.ai.javafx.base.utils.Random;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static com.github.teocci.algo.ai.javafx.base.model.dot.Dot.BASE_RADIUS;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-23
 */
public class GenerationController
{
    private static final String TAG = LogHelper.makeLogTag(GenerationController.class);

    public static final int BASE_OFFSET = 20 + BASE_RADIUS;

    private int size, width, height;
    private Pane canvas;


    private Label genValue, bestValue;

    private Dot[] dots;

    private double fitnessSum;
    private int gen = 0;

    private int bestDot = 0; //the index of the best dot in the dots[]

    private int minStep = 1000;

    private Dot goal;

    public GenerationController(int size, Pane canvas)
    {
        this.size = size;
        this.canvas = canvas;

        init();
    }

    public GenerationController(int size, Pane canvas, Label genValue, Label bestValue)
    {
        this.size = size;
        this.canvas = canvas;
        this.genValue = genValue;
        this.bestValue = bestValue;

        init();
    }

    private void init()
    {
        this.width = (int) canvas.getWidth();
        this.height = (int) canvas.getHeight();

        LogHelper.w(TAG, "(width, height)-> (" + width + ", " + height + ')');

        initDots();
        initGoal();

        initCanvas();
    }

    private void initCanvas()
    {
        canvas.getChildren().addAll(getDots());
    }

    private void initDots()
    {
        dots = new Dot[size];
        for (int i = 0; i < size; i++) {
            // Start the dots at the bottom of the window with a no velocity or acceleration
            dots[i] = new Dot(width / 2, height - BASE_OFFSET);
        }
    }

    private void initGoal()
    {
        int radius = 15;
        goal = new Dot(width / 2, radius);
        goal.setColor(Color.BLUE);
        goal.setRadius(radius);
    }


    public Circle[] getDots()
    {
        Circle[] nodes = new Circle[dots.length + 1];
        nodes[0] = goal.getDot();
        for (int i = 0; i < dots.length; i++) {
            nodes[i + 1] = dots[i].getDot();
        }

        return nodes;
    }

    //update all dots
    public void update()
    {
        for (int i = 0; i < dots.length; i++) {
            if (dots[i].getDna().getStep() > minStep) {
                // If the dot has already taken more steps than the best dot has taken to reach the goal
                dots[i].die();
            } else {
                if (canvas == null) throw new NullPointerException("Canvas is null.");
                dots[i].update(goal, width, height - BASE_OFFSET * 2 / 3);
            }
        }

        canvas.getChildren().clear();
        canvas.getChildren().addAll(getDots());

        if (genValue != null) genValue.setText("" + gen);
        if (bestValue != null) bestValue.setText("" + minStep);
    }

    // calculate all the fitness's
    public void calculateFitness()
    {
        for (int i = 0; i < dots.length; i++) {
            dots[i].calculateFitness(goal.getPos());
        }
    }


    /**
     * Returns whether all the dots are either dead or have reached the goal
     */
    public boolean allDotsDead()
    {
        for (Dot dot : dots) {
            if (!dot.isDead() && !dot.hasReachedGoal()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the next generation of dots
     */
    public void naturalSelection()
    {
        int length = dots.length - 1;

        // New generation
        Dot[] newDots = new Dot[length + 1];
        setBestDot();
        calculateFitnessSum();

        // The champion lives on
        newDots[length] = dots[bestDot].mitosis();
        newDots[length].best();

        for (int i = 0; i < length; i++) {
            // select parent based on fitness
            Dot parent = selectParent();
            if (parent == null) throw new NullPointerException("Parent is null.");

            // Get identical clone
            newDots[i] = parent.mitosis();
        }

        dots = newDots.clone();
        gen++;
    }


    public void calculateFitnessSum()
    {
        fitnessSum = 0;
        for (Dot dot : dots) {
            fitnessSum += dot.getFitness();
        }
    }

    //chooses dot from the population to return randomly(considering fitness)

    //this function works by randomly choosing a value between 0 and the sum of all the fitnesses
    //then go through all the dots and add their fitness to a running sum and if that sum is greater than the random value generated that dot is chosen
    //since dots with a higher fitness function add more to the running sum then they have a higher chance of being chosen
    private Dot selectParent()
    {
        double rand = Random.uniform(0, fitnessSum);

        double runningSum = 0;

        for (Dot dot : dots) {
            runningSum += dot.getFitness();
            if (runningSum > rand) {
                return dot;
            }
        }

        // Should never get to this point

        return null;
    }


    /**
     * Mutates all the brains of the babies
     */
    public void mutate()
    {
        for (int i = 1; i < dots.length; i++) {
            dots[i].getDna().mutate();
        }
    }

    /**
     * Finds the dot with the highest fitness and sets it as the best dot
     */
    public void setBestDot()
    {
        double max = 0;
        int maxIndex = 0;
        for (int i = 0; i < dots.length; i++) {
            if (dots[i].getFitness() > max) {
                max = dots[i].getFitness();
                maxIndex = i;
            }
        }

        bestDot = maxIndex;

        // if this dot reached the goal then reset the minimum number of steps it takes to get to the goal
        if (dots[bestDot].hasReachedGoal()) {
            if (dots[bestDot].getDna() != null) {
                minStep = dots[bestDot].getDna().getStep();
                LogHelper.e(TAG, "step:", minStep);
            }
        }
    }

    public int getGen()
    {
        return gen;
    }
}
