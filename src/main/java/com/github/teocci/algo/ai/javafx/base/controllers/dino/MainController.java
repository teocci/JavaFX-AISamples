package com.github.teocci.algo.ai.javafx.base.controllers.dino;

import com.github.teocci.algo.ai.javafx.base.model.dino.Bird;
import com.github.teocci.algo.ai.javafx.base.model.dino.Ground;
import com.github.teocci.algo.ai.javafx.base.model.dino.Obstacle;
import com.github.teocci.algo.ai.javafx.base.model.dino.Player;
import com.github.teocci.algo.ai.javafx.base.utils.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-30
 */
public class MainController
{
    private int nextConnectionNo = 1000;
    private Population population;
    private int frameSpeed = 60;


    private boolean showBestEachGen = false;
    private int upToGen = 0;
    private Player genPlayerTemp;

    boolean showNothing = false;


    //images
    PImage dinoRun1;
    PImage dinoRun2;
    PImage dinoJump;
    PImage dinoDuck;
    PImage dinoDuck1;
    PImage smallCactus;
    PImage manySmallCactus;
    PImage bigCactus;
    PImage bird;
    PImage bird1;


    private List<Obstacle> obstacles = new ArrayList<>();
    private ArrayList<Bird> birds = new ArrayList<>();
    private ArrayList<Ground> grounds = new ArrayList<>();


    private int obstacleTimer = 0;
    private int minimumTimeBetweenObstacles = 60;
    private int randomAddition = 0;
    private int groundCounter = 0;
    private float speed = 10;

    private int groundHeight = 250;
    private int playerXpos = 150;

    private List<Integer> obstacleHistory = new ArrayList<>();
    private List<Integer> randomAdditionHistory = new ArrayList<>();


//--------------------------------------------------------------------------------------------------------------------------------------------------

    void setup()
    {

        frameRate(60);
        fullScreen();
        dinoRun1 = loadImage("dinorun0000.png");
        dinoRun2 = loadImage("dinorun0001.png");
        dinoJump = loadImage("dinoJump0000.png");
        dinoDuck = loadImage("dinoduck0000.png");
        dinoDuck1 = loadImage("dinoduck0001.png");

        smallCactus = loadImage("cactusSmall0000.png");
        bigCactus = loadImage("cactusBig0000.png");
        manySmallCactus = loadImage("cactusSmallMany0000.png");
        bird = loadImage("berd.png");
        bird1 = loadImage("berd2.png");

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


    //---------------------------------------------------------------------------------------------------------------------------------------------------------
//draws the display screen
    void drawToScreen()
    {
        if (!showNothing) {
            background(250);
            stroke(0);
            strokeWeight(2);
            line(0, height - groundHeight - 30, width, height - groundHeight - 30);
            drawBrain();
            writeInfo();
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    void drawBrain()
    {  //show the brain of whatever genome is currently showing
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

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//writes info about the current player
    void writeInfo()
    {
        fill(200);
        textAlign(LEFT);
        textSize(40);
        if (showBestEachGen) { //if showing the best for each gen then write the applicable info
            text("Score: " + genPlayerTemp.score, 30, height - 30);
            //text(, width/2-180, height-30);
            textAlign(RIGHT);
            text("Gen: " + (genPlayerTemp.gen + 1), width - 40, height - 30);
            textSize(20);
            int x = 580;
            text("Distace to next obstacle", x, 18 + 44.44444);
            text("Height of obstacle", x, 18 + 2 * 44.44444);
            text("Width of obstacle", x, 18 + 3 * 44.44444);
            text("Bird height", x, 18 + 4 * 44.44444);
            text("Speed", x, 18 + 5 * 44.44444);
            text("Players Y position", x, 18 + 6 * 44.44444);
            text("Gap between obstacles", x, 18 + 7 * 44.44444);
            text("Bias", x, 18 + 8 * 44.44444);

            textAlign(LEFT);
            text("Small Jump", 1220, 118);
            text("Big Jump", 1220, 218);
            text("Duck", 1220, 318);
        } else { //evolving normally
            text("Score: " + floor(population.populationLife / 3.0), 30, height - 30);
            //text(, width/2-180, height-30);
            textAlign(RIGHT);

            text("Gen: " + (population.gen + 1), width - 40, height - 30);
            textSize(20);
            int x = 580;
            text("Distace to next obstacle", x, 18 + 44.44444);
            text("Height of obstacle", x, 18 + 2 * 44.44444);
            text("Width of obstacle", x, 18 + 3 * 44.44444);
            text("Bird height", x, 18 + 4 * 44.44444);
            text("Speed", x, 18 + 5 * 44.44444);
            text("Players Y position", x, 18 + 6 * 44.44444);
            text("Gap between obstacles", x, 18 + 7 * 44.44444);
            text("Bias", x, 18 + 8 * 44.44444);

            textAlign(LEFT);
            text("Small Jump", 1220, 118);
            text("Big Jump", 1220, 218);
            text("Duck", 1220, 318);
        }
    }


//--------------------------------------------------------------------------------------------------------------------------------------------------

    void keyPressed()
    {
        switch (key) {
            case '+'://speed up frame rate
                frameSpeed += 10;
                frameRate(frameSpeed);
                println(frameSpeed);
                break;
            case '-'://slow down frame rate
                if (frameSpeed > 10) {
                    frameSpeed -= 10;
                    frameRate(frameSpeed);
                    println(frameSpeed);
                }
                break;
            case 'g'://show generations
                showBestEachGen = !showBestEachGen;
                upToGen = 0;
                genPlayerTemp = population.getGenPlayers().get(upToGen).cloneForReplay();
                break;
            case 'n'://show absolutely nothing in order to speed up computation
                showNothing = !showNothing;
                break;
            case CODED://any of the arrow keys
                switch (keyCode) {
                    case RIGHT://right is used to move through the generations
                        if (showBestEachGen) {//if showing the best player each generation then move on to the next generation
                            upToGen++;
                            if (upToGen >= population.getGenPlayers().size()) {//if reached the current generation then exit out of the showing generations mode
                                showBestEachGen = false;
                            } else {
                                genPlayerTemp = population.getGenPlayers().get(upToGen).cloneForReplay();
                            }
                            break;
                        }
                        break;
                }
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
//called every frame
    void updateObstacles()
    {
        obstacleTimer++;
        speed += 0.002;
        if (obstacleTimer > minimumTimeBetweenObstacles + randomAddition) { //if the obstacle timer is high enough then add a new obstacle
            addObstacle();
        }
        groundCounter++;
        if (groundCounter > 10) { //every 10 frames add a ground bit
            groundCounter = 0;
            grounds.add(new Ground());
        }

        moveObstacles();//move everything
        if (!showNothing) {//show everything
            showObstacles();
        }
    }

    /**
     * Moves obstacles to the left based on the speed of the game
     */
    void moveObstacles()
    {
        println(speed);
        for (int i = 0; i < obstacles.size(); i++) {
            obstacles.get(i).move(speed);
            if (obstacles.get(i).posX < -playerXpos) {
                obstacles.remove(i);
                i--;
            }
        }

        for (int i = 0; i < birds.size(); i++) {
            birds.get(i).move(speed);
            if (birds.get(i).posX < -playerXpos) {
                birds.remove(i);
                i--;
            }
        }
        for (int i = 0; i < grounds.size(); i++) {
            grounds.get(i).move(speed);
            if (grounds.get(i).posX < -playerXpos) {
                grounds.remove(i);
                i--;
            }
        }
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
}
