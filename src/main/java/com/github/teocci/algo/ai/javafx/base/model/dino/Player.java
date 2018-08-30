package com.github.teocci.algo.ai.javafx.base.model.dino;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-30
 */
public class Player
{
    private double fitness;

    private Genome brain;

    private double unadjustedFitness;
    private int lifespan = 0;//how long the player lived for fitness
    private int bestScore = 0;//stores the score achieved used for replay

    private boolean dead;

    private int score;
    private int gen = 0;

    private int genomeInputs = 7;
    private int genomeOutputs = 3;

    private double[] vision = new double[genomeInputs];//t he input array fed into the neuralNet
    private double[] decision = new double[genomeOutputs]; //the out put of the NN

    private double posY = 0;
    private double velY = 0;
    private double gravity = 1.2;

    private int runCount = -5;
    private int size = 20;

    private List<Obstacle> replayObstacles = new ArrayList<>();
    private List<Bird> replayBirds = new ArrayList<>();
    private List<Integer> localObstacleHistory = new ArrayList<>();
    private List<Integer> localRandomAdditionHistory = new ArrayList<>();

    private int historyCounter = 0;
    private int localObstacleTimer = 0;
    private double localSpeed = 10;
    private int localRandomAddition = 0;


    private boolean replay = false;
    private boolean duck = false;

    public Player()
    {
        brain = new Genome(genomeInputs, genomeOutputs);
    }

    /**
     * Show the dino
     */
    public void show()
    {
        if (duck && posY == 0) {
            if (runCount < 0) {
                image(dinoDuck, playerXpos - dinoDuck.width / 2, height - groundHeight - (posY + dinoDuck.height));
            } else {

                image(dinoDuck1, playerXpos - dinoDuck1.width / 2, height - groundHeight - (posY + dinoDuck1.height));
            }
        } else if (posY == 0) {
            if (runCount < 0) {
                image(dinoRun1, playerXpos - dinoRun1.width / 2, height - groundHeight - (posY + dinoRun1.height));
            } else {
                image(dinoRun2, playerXpos - dinoRun2.width / 2, height - groundHeight - (posY + dinoRun2.height));
            }
        } else {
            image(dinoJump, playerXpos - dinoJump.width / 2, height - groundHeight - (posY + dinoJump.height));
        }
        runCount++;
        if (runCount > 5) {
            runCount = -5;
        }
    }

    public void incrementCounters()
    {
        lifespan++;
        if (lifespan % 3 == 0) {
            score += 1;
        }
    }

    /**
     * Checks for collisions and if this is a replay move all the obstacles
     */
    public void move()
    {
        posY += velY;
        if (posY > 0) {
            velY -= gravity;
        } else {
            velY = 0;
            posY = 0;
        }

        if (!replay) {
            for (int i = 0; i < obstacles.size(); i++) {
                if (obstacles.get(i).collided(playerXpos, posY + dinoRun1.height / 2, dinoRun1.width * 0.5, dinoRun1.height)) {
                    die();
                }
            }

            for (int i = 0; i < birds.size(); i++) {
                if (duck && posY == 0) {
                    if (birds.get(i).collided(playerXpos, posY + dinoDuck.height / 2, dinoDuck.width * 0.8, dinoDuck.height)) {
                        die();
                    }
                } else {
                    if (birds.get(i).collided(playerXpos, posY + dinoRun1.height / 2, dinoRun1.width * 0.5, dinoRun1.height)) {
                        die();
                    }
                }
            }
        } else {
            // If replaying then move local obstacles
            for (Obstacle replayObstacle : replayObstacles) {
                if (replayObstacle.collided(playerXpos, posY + dinoRun1.height / 2, dinoRun1.width * 0.5, dinoRun1.height)) {
                    die();
                }
            }


            for (Bird replayBird : replayBirds) {
                if (duck && posY == 0) {
                    if (replayBird.collided(playerXpos, posY + dinoDuck.height / 2, dinoDuck.width * 0.8, dinoDuck.height)) {
                        die();
                    }
                } else {
                    if (replayBird.collided(playerXpos, posY + dinoRun1.height / 2, dinoRun1.width * 0.5, dinoRun1.height)) {
                        die();
                    }
                }
            }
        }
    }

    public void jump(boolean bigJump)
    {
        if (posY == 0) {
            if (bigJump) {
                gravity = 1;
                velY = 20;
            } else {
                gravity = 1.2;
                velY = 16;
            }
        }
    }

    /**
     * If parameter is true and is in the air increase gravity
     */
    public void ducking(boolean isDucking)
    {
        if (posY != 0 && isDucking) {
            gravity = 3;
        }
        duck = isDucking;
    }

    /**
     * Called every frame
     */
    public void update()
    {
        incrementCounters();
        move();
    }

    /**
     * Get inputs for Neural network
     */
    public void look()
    {
        if (!replay) {
            float temp = 0;
            float min = 10000;
            int minIndex = -1;
            boolean berd = false;
            for (int i = 0; i < obstacles.size(); i++) {
                if (obstacles.get(i).posX + obstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) < min && obstacles.get(i).posX + obstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) > 0) {//if the distance between the left of the player and the right of the obstacle is the least
                    min = obstacles.get(i).posX + obstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2);
                    minIndex = i;
                }
            }

            for (int i = 0; i < birds.size(); i++) {
                if (birds.get(i).posX + birds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) < min && birds.get(i).posX + birds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) > 0) {//if the distance between the left of the player and the right of the obstacle is the least
                    min = birds.get(i).posX + birds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2);
                    minIndex = i;
                    berd = true;
                }
            }
            vision[4] = speed;
            vision[5] = posY;


            if (minIndex == -1) {//if there are no obstacles
                vision[0] = 0;
                vision[1] = 0;
                vision[2] = 0;
                vision[3] = 0;
                vision[6] = 0;
            } else {

                vision[0] = 1.0 / (min / 10.0);
                if (berd) {
                    vision[1] = birds.get(minIndex).h;
                    vision[2] = birds.get(minIndex).w;
                    if (birds.get(minIndex).typeOfBird == 0) {
                        vision[3] = 0;
                    } else {
                        vision[3] = birds.get(minIndex).posY;
                    }
                } else {
                    vision[1] = obstacles.get(minIndex).h;
                    vision[2] = obstacles.get(minIndex).w;
                    vision[3] = 0;
                }


                //vision 6 is the gap between the this obstacle and the next one
                int bestIndex = minIndex;
                float closestDist = min;
                min = 10000;
                minIndex = -1;
                for (int i = 0; i < obstacles.size(); i++) {
                    if ((berd || i != bestIndex) && obstacles.get(i).posX + obstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) < min && obstacles.get(i).posX + obstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) > 0) {//if the distance between the left of the player and the right of the obstacle is the least
                        min = obstacles.get(i).posX + obstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2);
                        minIndex = i;
                    }
                }

                for (int i = 0; i < birds.size(); i++) {
                    if ((!berd || i != bestIndex) && birds.get(i).posX + birds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) < min && birds.get(i).posX + birds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) > 0) {//if the distance between the left of the player and the right of the obstacle is the least
                        min = birds.get(i).posX + birds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2);
                        minIndex = i;
                    }
                }

                if (minIndex == -1) {//if there is only one obejct on the screen
                    vision[6] = 0;
                } else {
                    vision[6] = 1 / (min - closestDist);
                }
            }
        } else {//if replaying then use local shit
            float temp = 0;
            float min = 10000;
            int minIndex = -1;
            boolean berd = false;
            for (int i = 0; i < replayObstacles.size(); i++) {
                if (replayObstacles.get(i).posX + replayObstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) < min && replayObstacles.get(i).posX + replayObstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) > 0) {//if the distance between the left of the player and the right of the obstacle is the least
                    min = replayObstacles.get(i).posX + replayObstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2);
                    minIndex = i;
                }
            }

            for (int i = 0; i < replayBirds.size(); i++) {
                if (replayBirds.get(i).posX + replayBirds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) < min && replayBirds.get(i).posX + replayBirds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) > 0) {//if the distance between the left of the player and the right of the obstacle is the least
                    min = replayBirds.get(i).posX + replayBirds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2);
                    minIndex = i;
                    berd = true;
                }
            }
            vision[4] = localSpeed;
            vision[5] = posY;


            if (minIndex == -1) {//if there are no replayObstacles
                vision[0] = 0;
                vision[1] = 0;
                vision[2] = 0;
                vision[3] = 0;
                vision[6] = 0;
            } else {

                vision[0] = 1.0 / (min / 10.0);
                if (berd) {
                    vision[1] = replayBirds.get(minIndex).h;
                    vision[2] = replayBirds.get(minIndex).w;
                    if (replayBirds.get(minIndex).typeOfBird == 0) {
                        vision[3] = 0;
                    } else {
                        vision[3] = replayBirds.get(minIndex).posY;
                    }
                } else {
                    vision[1] = replayObstacles.get(minIndex).h;
                    vision[2] = replayObstacles.get(minIndex).w;
                    vision[3] = 0;
                }


                //vision 6 is the gap between the this obstacle and the next one
                int bestIndex = minIndex;
                float closestDist = min;
                min = 10000;
                minIndex = -1;
                for (int i = 0; i < replayObstacles.size(); i++) {
                    if ((berd || i != bestIndex) && replayObstacles.get(i).posX + replayObstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) < min && replayObstacles.get(i).posX + replayObstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) > 0) {//if the distance between the left of the player and the right of the obstacle is the least
                        min = replayObstacles.get(i).posX + replayObstacles.get(i).w / 2 - (playerXpos - dinoRun1.width / 2);
                        minIndex = i;
                    }
                }

                for (int i = 0; i < replayBirds.size(); i++) {
                    if ((!berd || i != bestIndex) && replayBirds.get(i).posX + replayBirds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) < min && replayBirds.get(i).posX + replayBirds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2) > 0) {//if the distance between the left of the player and the right of the obstacle is the least
                        min = replayBirds.get(i).posX + replayBirds.get(i).w / 2 - (playerXpos - dinoRun1.width / 2);
                        minIndex = i;
                    }
                }

                if (minIndex == -1) {//if there is only one obejct on the screen
                    vision[6] = 0;
                } else {
                    vision[6] = 1 / (min - closestDist);
                }
            }
        }
    }


    /**
     * Gets the output of the brain then converts them to actions
     */
    public void think()
    {
        double max = 0;
        int maxIndex = 0;
        //get the output of the neural network
        decision = brain.feedForward(vision);

        for (int i = 0; i < decision.length; i++) {
            if (decision[i] > max) {
                max = decision[i];
                maxIndex = i;
            }
        }

        if (max < 0.7) {
            ducking(false);
            return;
        }

        switch (maxIndex) {
            case 0:
                jump(false);
                break;
            case 1:
                jump(true);
                break;
            case 2:
                ducking(true);
                break;
        }
    }

    /**
     * Returns a clone of this player with the same brian
     */
    public Player clone()
    {
        Player clone = new Player();
        clone.brain = brain.clone();
        clone.fitness = fitness;
        clone.brain.generateNetwork();
        clone.gen = gen;
        clone.bestScore = score;
        return clone;
    }

    /**
     * Since there is some randomness in games sometimes when we want to replay the game we need to remove that randomness
     */
    public Player cloneForReplay()
    {
        Player clone = new Player();
        clone.brain = brain.clone();
        clone.fitness = fitness;
        clone.brain.generateNetwork();
        clone.gen = gen;
        clone.bestScore = score;
        clone.replay = true;
        if (replay) {
            clone.localObstacleHistory = (ArrayList) localObstacleHistory.clone();
            clone.localRandomAdditionHistory = (ArrayList) localRandomAdditionHistory.clone();
        } else {
            clone.localObstacleHistory = (ArrayList) obstacleHistory.clone();
            clone.localRandomAdditionHistory = (ArrayList) randomAdditionHistory.clone();
        }

        return clone;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //fot Genetic algorithm
    public void calculateFitness()
    {
        fitness = score * score;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    Player crossover(Player parent2)
    {
        Player child = new Player();
        child.brain = brain.crossover(parent2.brain);
        child.brain.generateNetwork();
        return child;
    }

    /**
     * If replaying then the dino has local obstacles
     */
    public void updateLocalObstacles()
    {
        localObstacleTimer++;
        localSpeed += 0.002;
        if (localObstacleTimer > minimumTimeBetweenObstacles + localRandomAddition) {
            addLocalObstacle();
        }
        groundCounter++;
        if (groundCounter > 10) {
            groundCounter = 0;
            grounds.add(new Ground());
        }

        moveLocalObstacles();
        showLocalObstacles();
    }

    public void moveLocalObstacles()
    {
        for (int i = 0; i < replayObstacles.size(); i++) {
            replayObstacles.get(i).move(localSpeed);
            if (replayObstacles.get(i).posX < -100) {
                replayObstacles.remove(i);
                i--;
            }
        }

        for (int i = 0; i < replayBirds.size(); i++) {
            replayBirds.get(i).move(localSpeed);
            if (replayBirds.get(i).posX < -100) {
                replayBirds.remove(i);
                i--;
            }
        }
        for (int i = 0; i < grounds.size(); i++) {
            grounds.get(i).move(localSpeed);
            if (grounds.get(i).posX < -100) {
                grounds.remove(i);
                i--;
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void addLocalObstacle()
    {
        int tempInt = localObstacleHistory.get(historyCounter);
        localRandomAddition = localRandomAdditionHistory.get(historyCounter);
        historyCounter++;
        if (tempInt < 3) {
            replayBirds.add(new Bird(tempInt));
        } else {
            replayObstacles.add(new Obstacle(tempInt - 3));
        }
        localObstacleTimer = 0;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    public void showLocalObstacles()
    {
        for (int i = 0; i < grounds.size(); i++) {
            grounds.get(i).show();
        }
        for (int i = 0; i < replayObstacles.size(); i++) {
            replayObstacles.get(i).show();
        }

        for (int i = 0; i < replayBirds.size(); i++) {
            replayBirds.get(i).show();
        }
    }

    public void die()
    {
        this.dead = true;
    }

    public void setDead(boolean dead)
    {
        this.dead = dead;
    }

    public Genome getBrain()
    {
        return brain;
    }

    public double getFitness()
    {
        return fitness;
    }

    public boolean isDead()
    {
        return dead;
    }
}
