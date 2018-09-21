package com.github.teocci.algo.ai.javafx.base.controllers.dot;

import com.github.teocci.algo.ai.javafx.base.model.dot.Dot;
import com.github.teocci.algo.ai.javafx.base.model.dot.Obstacle;
import com.github.teocci.algo.ai.javafx.base.model.dot.Vector2D;
import com.github.teocci.algo.ai.javafx.base.utils.CommonHelper;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import com.github.teocci.algo.ai.javafx.base.utils.Random;
import com.github.teocci.algo.ai.javafx.base.views.dot.MainView;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

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

    private MainView view;

    private int size, width, height;
//    private Pane canvas;

    private Dot[] dots;
    private Obstacle[] obstacles;

    private double fitnessSum;
    private int gen = 0;

    private int bestDot = 0; //the index of the best dot in the dots[]

    private int minStep = 1000;

    private Dot goal;

    public GenerationController(Stage stage) {
        view = new MainView(this, stage);
        size = 1000;

        init();
    }

    public GenerationController(int size, Pane canvas)
    {
//        this.size = size;
//        this.canvas = canvas;

        init();
    }

    public GenerationController(int size, Pane canvas, Label genValue, Label bestValue)
    {
        this.size = size;
//        this.canvas = canvas;
//        this.genValue = genValue;
//        this.bestValue = bestValue;

        init();
    }

    private void init()
    {
        this.width = (int) view.getCanvasWidth();
        this.height = (int) view.getCanvasHeight();

        LogHelper.w(TAG, "(width, height)-> (" + width + ", " + height + ')');

        initDots();
        initGoal();
        initObstacles();

        initCanvas();
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

    private void initObstacles()
    {
        obstacles = new Obstacle[2];
        obstacles[0] = new Obstacle(new Vector2D(0, 300), 500, 80);
        obstacles[1] = new Obstacle(new Vector2D(300, 600), width-300, 80);
    }

    private void initCanvas()
    {
        view.drawDots(CommonHelper.add2BArray(dots, goal));
    }

    //update all dots
    public void update()
    {
        for (Dot dot : dots) {
            if (dot.getDna().getStep() > minStep) {
                // If the dot has already taken more steps than the best dot has taken to reach the goal
                dot.die();
            } else {
                dot.update(goal, obstacles, width, height - BASE_OFFSET * 2 / 3);
            }
        }

        view.clear();
        view.drawObstacles(obstacles);
        view.drawDots(CommonHelper.add2BArray(dots, goal));
//        canvas.getChildren().clear();
//        canvas.getChildren().addAll(getDots());

        view.setGenValue(gen);
        view.setBestValue(minStep);
    }

    // calculate all the fitness's
    public void calculateFitness()
    {
        for (Dot dot : dots) {
            dot.calculateFitness(goal.getPos());
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


    private void calculateFitnessSum()
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
}
