package com.github.teocci.algo.ai.javafx.base.model.dino.chars;

import com.github.teocci.algo.ai.javafx.base.controllers.dino.MainController;
import com.github.teocci.algo.ai.javafx.base.model.dino.Element;
import com.github.teocci.algo.ai.javafx.base.model.dino.Genome;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

import static com.github.teocci.algo.ai.javafx.base.utils.CommonHelper.execMove;
import static com.github.teocci.algo.ai.javafx.base.utils.Config.*;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-30
 */
public class Player extends Element
{
    private static final String TAG = LogHelper.makeLogTag(Genome.class);

    private Image[] images = new Image[]{
            new Image(IMG_DINO_DUCK_00),
            new Image(IMG_DINO_DUCK_01),
            new Image(IMG_DINO_RUN_00),
            new Image(IMG_DINO_RUN_01),
            new Image(IMG_DINO_JUMP)
    };

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

    private final MainController controller;

    public Player()
    {
        brain = new Genome(genomeInputs, genomeOutputs);
        controller = MainController.getInstance();

    }

    @Override
    public void move(double speed)
    {
        posY += velY;
        if (posY > 0) {
            velY -= gravity;
        } else {
            velY = 0;
            posY = 0;
        }

        if (!replay) {
            globalMove(controller.getObstacles(), controller.getBirds());
        } else {
            globalMove(replayObstacles, replayBirds);
        }
    }

    /**
     * Show the dino
     */
    @Override
    public void show()
    {
//        int index = controller.getView().getCanvas().getChildren().indexOf(this);
//        controller.getView().getCanvas().getChildren().set(index, this);
//        Platform.runLater(() -> setFill(loadImage(getType())));
        updateImage(getType());
        runCount++;
        if (runCount > 5) {
            runCount = -5;
        }
    }

    @Override
    public boolean collided(double posX, double v, double v1, double height)
    {
        return false;
    }

    @Override
    public double getPosX()
    {
        return -1;
    }

    @Override
    public double getPosY()
    {
        return posY;
    }

    @Override
    public int getW()
    {
        return -1;
    }

    @Override
    public int getH()
    {
        return -1;
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
        move(0);
    }

    private int getType()
    {
        if (duck && posY == 0) {
            if (runCount < 0) {
                return 0;
            } else {
                return 1;
            }
        } else if (posY == 0) {
            if (runCount < 0) {
                return 2;
            } else {
                return 3;
            }
        } else {
            return 4;
        }
    }

    private void updateImage(int type)
    {
        Image image = images[type];
        int posX = controller.getPlayerXPos();
        double height = controller.getHeight();
        int groundHeight = controller.getGroundHeight();

        // Draw next image
        GraphicsContext gc = MainController.getInstance().getView().getGc();
        gc.drawImage(image, posX - image.getWidth() / 2, height - groundHeight - (posY + image.getHeight()));
    }

    private Paint loadImage(int type)
    {
        Image image = images[type];
        int posX = controller.getPlayerXPos();
        double height = controller.getHeight();
        int groundHeight = controller.getGroundHeight();

        return new ImagePattern(image, 0, 0, posX - image.getWidth() / 2, height - groundHeight - (posY + image.getHeight()), true);
    }

    private void globalMove(List<Obstacle> obstacles, List<Bird> birds)
    {
        for (Obstacle obstacle : obstacles) {
            calculateCollision(obstacle, images[DINO_RUN_00]);
        }

        for (Bird bird : birds) {
            if (duck && posY == 0) {
                calculateCollision(bird, images[DINO_DUCK_00]);
            } else {
                calculateCollision(bird, images[DINO_RUN_00]);
            }
        }
    }

    private void calculateCollision(Element element, Image player)
    {
        int posX = controller.getPlayerXPos();
        if (element.collided(posX, posY + player.getHeight() / 2, player.getWidth() * 0.5, player.getHeight())) {
            die();
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
            loadVision(controller.getObstacles(), controller.getBirds());
        } else {//if replaying then use local shit
            loadVision(replayObstacles, replayBirds);
        }
    }

    private void loadVision(List<Obstacle> obstacles, List<Bird> birds)
    {
        double temp = 0;
        double min = 10000;
        int minIndex = -1;
        boolean birdFound = false;

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            if (isLeastDistance(obstacle, min)) {
                min = calculateDistance(obstacle);
                minIndex = i;
            }
        }

        for (int i = 0; i < birds.size(); i++) {
            Bird bird = birds.get(i);
            if (isLeastDistance(bird, min)) {
                min = calculateDistance(bird);
                minIndex = i;
                birdFound = true;
            }
        }
        vision[4] = controller.getSpeed();
        vision[5] = posY;

        // If there are no obstacles
        if (minIndex == -1) {
            emptyVision();
        } else {
            vision[0] = 1.0 / (min / 10.0);
            if (birdFound) {
                vision[1] = birds.get(minIndex).getH();
                vision[2] = birds.get(minIndex).getW();
                if (birds.get(minIndex).getTypeOfBird() == 0) {
                    vision[3] = 0;
                } else {
                    vision[3] = birds.get(minIndex).getPosY();
                }
            } else {
                vision[1] = obstacles.get(minIndex).getH();
                vision[2] = obstacles.get(minIndex).getW();
                vision[3] = 0;
            }


            // Vision 6 is the gap between the this obstacle and the next one
            int bestIndex = minIndex;
            double closestDist = min;
            min = 10000;
            minIndex = -1;
            for (int i = 0; i < obstacles.size(); i++) {
                Obstacle obstacle = obstacles.get(i);
                if ((birdFound || i != bestIndex) && isLeastDistance(obstacle, min)) {
                    min = calculateDistance(obstacle);
                    minIndex = i;
                }
            }

            for (int i = 0; i < birds.size(); i++) {
                Bird bird = birds.get(i);
                if ((!birdFound || i != bestIndex) && isLeastDistance(bird, min)) {
                    min = calculateDistance(bird);
                    minIndex = i;
                }
            }

            // If there is only one object on the screen
            if (minIndex == -1) {
                vision[6] = 0;
            } else {
                vision[6] = 1 / (min - closestDist);
            }
        }
    }

    /**
     * Returns true if the distance between the left of the player and the right of the obstacle is the least
     */
    private boolean isLeastDistance(Element element, double min)
    {
        double distance = calculateDistance(element);

        return distance > 0 && distance < min;
    }

    private double calculateDistance(Element element)
    {
        Image frame = images[DINO_RUN_00];
        int posX = controller.getPlayerXPos();
        return element.getPosX() + element.getW() / (double) 2 - (posX - frame.getWidth() / 2);
    }

    private void emptyVision()
    {
        vision[0] = 0;
        vision[1] = 0;
        vision[2] = 0;
        vision[3] = 0;
        vision[6] = 0;
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
            clone.localObstacleHistory = new ArrayList<>(localObstacleHistory);
            clone.localRandomAdditionHistory = new ArrayList<>(localRandomAdditionHistory);
        } else {
            clone.localObstacleHistory = new ArrayList<>(controller.getObstacleHistory());
            clone.localRandomAdditionHistory = new ArrayList<>(controller.getRandomAdditionHistory());
        }

        return clone;
    }

    /**
     * Fitness function for Genetic algorithm
     */
    public void calculateFitness()
    {
        fitness = score * score;
    }

    public Player crossover(Player parent2)
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
        if (localObstacleTimer > controller.getMinimumTimeBetweenObstacles() + localRandomAddition) {
            addLocalObstacle();
        }

        controller.increaseGroundCounter();
        if (controller.getGroundCounter() > 10) {
            controller.setGroundCounter(0);
            controller.getGrounds().add(new Ground());
        }

        moveLocalObstacles();
        showLocalObstacles();
    }

    public void moveLocalObstacles()
    {
        execMove(replayObstacles, localSpeed, -100);
        execMove(replayBirds, localSpeed, -100);
        execMove(controller.getGrounds(), localSpeed, -100);
    }

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

    public void showLocalObstacles()
    {
        showRequest(controller.getGrounds());
        showRequest(replayObstacles);
        showRequest(replayBirds);
    }

    private void showRequest(List<? extends Element> elements)
    {
        for (Element element : elements) {
            element.show();
        }
    }

    public void die()
    {
        this.dead = true;
    }

    public void setFiness(double fitness)
    {
        this.fitness = fitness;
    }

    public void setDead(boolean dead)
    {
        this.dead = dead;
    }

    public void setGen(int gen)
    {
        this.gen = gen;
    }

    public void setScore(int score)
    {
        this.score = score;
    }


    public Genome getBrain()
    {
        return brain;
    }

    public double getFitness()
    {
        return fitness;
    }

    public int getScore()
    {
        return score;
    }

    public int getGen()
    {
        return gen;
    }


    public boolean isDead()
    {
        return dead;
    }
}
