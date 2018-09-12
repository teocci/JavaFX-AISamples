package com.github.teocci.algo.ai.javafx.base.controllers.dino;

import com.github.teocci.algo.ai.javafx.base.model.dino.Bird;
import com.github.teocci.algo.ai.javafx.base.model.dino.Genome;
import com.github.teocci.algo.ai.javafx.base.model.dino.Ground;
import com.github.teocci.algo.ai.javafx.base.model.dino.Obstacle;
import com.github.teocci.algo.ai.javafx.base.model.dino.Player;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import com.github.teocci.algo.ai.javafx.base.utils.Random;

import java.util.ArrayList;
import java.util.List;

import static com.github.teocci.algo.ai.javafx.base.utils.CommonHelper.execMove;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-30
 */
public class MainController
{
    private static final String TAG = LogHelper.makeLogTag(Genome.class);

    private int nextConnectionNo = 1000;
    private Population population;
    private int frameSpeed = 60;

    private boolean showBestEachGen = false;
    private int upToGen = 0;
    private Player genPlayerTemp;

    boolean showNothing = false;

    private List<Obstacle> obstacles = new ArrayList<>();
    private ArrayList<Bird> birds = new ArrayList<>();
    private ArrayList<Ground> grounds = new ArrayList<>();

    private int obstacleTimer = 0;

    private int minimumTimeBetweenObstacles = 60;
    private int randomAddition = 0;
    private int groundCounter = 0;
    private double speed = 10;

    private int groundHeight = 250;
    private int playerXPos = 150;

    private int width = 1280;
    private int height = 720;

    private List<Integer> obstacleHistory = new ArrayList<>();
    private List<Integer> randomAdditionHistory = new ArrayList<>();

    private static volatile MainController instance;
    private static Object mutex = new Object();

    private MainController()
    {
    }

    public static MainController getInstance()
    {
        MainController result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new MainController();
            }
        }
        return result;
    }

    void setup()
    {
//        frameRate(60);
//        fullScreen();

        population = new Population(500); //<<number of dinosaurs in each generation
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    void draw()
    {
        drawToScreen();
        if (showBestEachGen) {//show the best of each gen
            if (!genPlayerTemp.isDead()) {//if current gen player is not dead then update it
                genPlayerTemp.updateLocalObstacles();
                genPlayerTemp.look();
                genPlayerTemp.think();
                genPlayerTemp.update();
                genPlayerTemp.show();
            } else {//if dead move on to the next generation
                upToGen++;
                if (upToGen >= population.getGenPlayers().size()) {//if at the end then return to the start and stop doing it
                    upToGen = 0;
                    showBestEachGen = false;
                } else {//if not at the end then get the next generation
                    genPlayerTemp = population.getGenPlayers().get(upToGen).cloneForReplay();
                }
            }
        } else {//if just evolving normally
            if (!population.done()) {//if any players are alive then update them
                updateObstacles();
                population.updateAlive();
            } else {//all dead
                //genetic algorithm
                population.naturalSelection();
                resetObstacles();
            }
        }
    }


    /**
     * Draws the display screen
     */
    void drawToScreen()
    {
        if (!showNothing) {
//            background(250);
//            stroke(0);
//            strokeWeight(2);
//            line(0, height - groundHeight - 30, width, height - groundHeight - 30);
            drawBrain();
            writeInfo();
        }
    }

    void drawBrain()
    {
        // Show the brain of whatever genome is currently showing
        int startX = 600;
        int startY = 10;
        int w = 600;
        int h = 400;

        if (showBestEachGen) {
            genPlayerTemp.getBrain().drawGenome(startX, startY, w, h);
        } else {
            for (int i = 0; i < population.getPlayers().size(); i++) {
                if (!population.getPlayers().get(i).isDead()) {
                    population.getPlayers().get(i).getBrain().drawGenome(startX, startY, w, h);
                    break;
                }
            }
        }
    }

    /**
     * Writes info about the current player
     */
    void writeInfo()
    {
//        fill(200);
//        textAlign(LEFT);
//        textSize(40);
        if (showBestEachGen) { //if showing the best for each gen then write the applicable info
//            text("Score: " + genPlayerTemp.score, 30, height - 30);
            //text(, width/2-180, height-30);
//            textAlign(RIGHT);
//            text("Gen: " + (genPlayerTemp.gen + 1), width - 40, height - 30);
//            textSize(20);
            int x = 580;
//            text("Distace to next obstacle", x, 18 + 44.44444);
//            text("Height of obstacle", x, 18 + 2 * 44.44444);
//            text("Width of obstacle", x, 18 + 3 * 44.44444);
//            text("Bird height", x, 18 + 4 * 44.44444);
//            text("Speed", x, 18 + 5 * 44.44444);
//            text("Players Y position", x, 18 + 6 * 44.44444);
//            text("Gap between obstacles", x, 18 + 7 * 44.44444);
//            text("Bias", x, 18 + 8 * 44.44444);
//
//            textAlign(LEFT);
//            text("Small Jump", 1220, 118);
//            text("Big Jump", 1220, 218);
//            text("Duck", 1220, 318);
        } else { //evolving normally
//            text("Score: " + floor(population.populationLife / 3.0), 30, height - 30);
            //text(, width/2-180, height-30);
//            textAlign(RIGHT);
//
//            text("Gen: " + (population.gen + 1), width - 40, height - 30);
//            textSize(20);
            int x = 580;
//            text("Distace to next obstacle", x, 18 + 44.44444);
//            text("Height of obstacle", x, 18 + 2 * 44.44444);
//            text("Width of obstacle", x, 18 + 3 * 44.44444);
//            text("Bird height", x, 18 + 4 * 44.44444);
//            text("Speed", x, 18 + 5 * 44.44444);
//            text("Players Y position", x, 18 + 6 * 44.44444);
//            text("Gap between obstacles", x, 18 + 7 * 44.44444);
//            text("Bias", x, 18 + 8 * 44.44444);
//
//            textAlign(LEFT);
//            text("Small Jump", 1220, 118);
//            text("Big Jump", 1220, 218);
//            text("Duck", 1220, 318);
        }
    }


    void keyPressed()
    {
//        switch (key) {
//            case '+'://speed up frame rate
//                frameSpeed += 10;
//                frameRate(frameSpeed);
//                println(frameSpeed);
//                break;
//            case '-'://slow down frame rate
//                if (frameSpeed > 10) {
//                    frameSpeed -= 10;
//                    frameRate(frameSpeed);
//                    println(frameSpeed);
//                }
//                break;
//            case 'g'://show generations
//                showBestEachGen = !showBestEachGen;
//                upToGen = 0;
//                genPlayerTemp = population.getGenPlayers().get(upToGen).cloneForReplay();
//                break;
//            case 'n'://show absolutely nothing in order to speed up computation
//                showNothing = !showNothing;
//                break;
//            case CODED://any of the arrow keys
//                switch (keyCode) {
//                    case RIGHT://right is used to move through the generations
//                        if (showBestEachGen) {//if showing the best player each generation then move on to the next generation
//                            upToGen++;
//                            if (upToGen >= population.getGenPlayers().size()) {//if reached the current generation then exit out of the showing generations mode
//                                showBestEachGen = false;
//                            } else {
//                                genPlayerTemp = population.getGenPlayers().get(upToGen).cloneForReplay();
//                            }
//                            break;
//                        }
//                        break;
//                }
//        }
    }

    /**
     * Called every frame
     */
    void updateObstacles()
    {
        obstacleTimer++;
        speed += 0.002;
        // Whenever a obstacle timer is high enough then add a new obstacle
        if (obstacleTimer > minimumTimeBetweenObstacles + randomAddition) {
            addObstacle();
        }
        groundCounter++;
        if (groundCounter > 10) { //every 10 frames add a ground bit
            groundCounter = 0;
            grounds.add(new Ground());
        }

        moveElements();//move everything
        if (!showNothing) {//show everything
            showObstacles();
        }
    }

    /**
     * Moves obstacles to the left based on the speed of the game
     */
    void moveElements()
    {
        LogHelper.e(TAG, "Speed: " + speed);
        execMove(obstacles, speed, playerXPos);
        execMove(birds, speed, playerXPos);
        execMove(grounds, speed, playerXPos);
    }

    /**
     * Every so often add an obstacle
     */
    void addObstacle()
    {
        int lifespan = population.getPopulationLife();

        int tempInt = Random.uniform(3);
        if (lifespan > 1000 && Random.uniform() < 0.15) { // 15% of the time add a bird
            Bird temp = new Bird(tempInt);//floor(random(3)));
            birds.add(temp);
        } else {//otherwise add a cactus
            Obstacle temp = new Obstacle(tempInt);//floor(random(3)));
            obstacles.add(temp);
            tempInt += 3;
        }
        obstacleHistory.add(tempInt);

        randomAddition = Random.uniform(50);
        randomAdditionHistory.add(randomAddition);
        obstacleTimer = 0;
    }

    public void showObstacles()
    {
        for (Ground ground : grounds) {
            ground.show();
        }
        for (Obstacle obstacle : obstacles) {
            obstacle.show();
        }

        for (Bird bird2 : birds) {
            bird2.show();
        }
    }

    /**
     * Resets all the obstacles after every dino has died
     */
    void resetObstacles()
    {
        randomAdditionHistory = new ArrayList<>();
        obstacleHistory = new ArrayList<>();

        obstacles = new ArrayList<>();
        birds = new ArrayList<>();

        obstacleTimer = 0;
        randomAddition = 0;
        groundCounter = 0;
        speed = 10;
    }

    public void increaseGroundCounter()
    {
        groundCounter++;
    }

    public void increaseNextConnectionNo()
    {
        nextConnectionNo++;
    }


    public void setNextConnectionNo(int nextConnectionNo)
    {
        this.nextConnectionNo = nextConnectionNo;
    }

    public void setShowNothing(boolean showNothing)
    {
        this.showNothing = showNothing;
    }

    public void setObstacles(List<Obstacle> obstacles)
    {
        this.obstacles = obstacles;
    }

    public void setBirds(ArrayList<Bird> birds)
    {
        this.birds = birds;
    }

    public void setGrounds(ArrayList<Ground> grounds)
    {
        this.grounds = grounds;
    }

    public void setObstacleHistory(List<Integer> obstacleHistory)
    {
        this.obstacleHistory = obstacleHistory;
    }

    public void setRandomAdditionHistory(List<Integer> randomAdditionHistory)
    {
        this.randomAdditionHistory = randomAdditionHistory;
    }

    public void setGroundCounter(int groundCounter)
    {
        this.groundCounter = groundCounter;
    }


    public int getNextConnectionNo()
    {
        return nextConnectionNo;
    }

    public List<Obstacle> getObstacles()
    {
        return obstacles;
    }

    public ArrayList<Bird> getBirds()
    {
        return birds;
    }

    public ArrayList<Ground> getGrounds()
    {
        return grounds;
    }

    public List<Integer> getObstacleHistory()
    {
        return obstacleHistory;
    }

    public List<Integer> getRandomAdditionHistory()
    {
        return randomAdditionHistory;
    }

    public int getMinimumTimeBetweenObstacles()
    {
        return minimumTimeBetweenObstacles;
    }

    public int getObstacleTimer()
    {
        return obstacleTimer;
    }

    public int getRandomAddition()
    {
        return randomAddition;
    }

    public int getGroundCounter()
    {
        return groundCounter;
    }

    public double getSpeed()
    {
        return speed;
    }

    public int getGroundHeight()
    {
        return groundHeight;
    }

    public int getPlayerXPos()
    {
        return playerXPos;
    }

    public double getWidth()
    {
        return width;
    }

    public double getHeight()
    {
        return height;
    }


    public boolean isShowNothing()
    {
        return showNothing;
    }
}
