package com.github.teocci.algo.ai.javafx.base.controllers.dino;

import com.github.teocci.algo.ai.javafx.base.connections.ConnectionHistory;
import com.github.teocci.algo.ai.javafx.base.model.dino.Genome;
import com.github.teocci.algo.ai.javafx.base.model.dino.chars.Player;
import com.github.teocci.algo.ai.javafx.base.model.dino.Species;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-30
 */
public class Population
{
    private static final String TAG = LogHelper.makeLogTag(Genome.class);

    private List<Player> players = new ArrayList<>();

    private Player bestPlayer; // The best ever player
    private int bestScore = 0; // Score of the best ever player
    private int gen;

    private List<ConnectionHistory> innovationHistory = new ArrayList<>();
    private List<Player> genPlayers = new ArrayList<>();
    private List<Species> species = new ArrayList<>();

    private boolean massExtinctionEvent = false;
    private boolean newStage = false;

    private int populationLife = 0;

    public Population(int size)
    {
        for (int i = 0; i < size; i++) {
            Player player = new Player();
            players.add(player);
            player.getBrain().generateNetwork();
            player.getBrain().mutate(innovationHistory);
        }
    }


    /**
     * Update all the players which are alive
     */
    public void updateAlive()
    {
        populationLife++;
        for (Player player : players) {
            if (!player.isDead()) {
                player.look();//get inputs for brain
                player.think();//use outputs from neural network
                player.update();//move the player according to the outputs from the neural network
                if (!MainController.getInstance().isShowNothing()) {
                    player.show();
                }
            }
        }
    }

    /**
     * Returns true if all the players are dead
     */
    public boolean done()
    {
        for (Player player : players) {
            if (!player.isDead()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the best player globally and for this gen
     */
    public void setBestPlayer()
    {
        Player tempBest = species.get(0).getPlayers().get(0);
        tempBest.setGen(gen);


        // If best this gen is better than the global best score then set the global best as the best this gen
        if (tempBest.getScore() > bestScore) {
            genPlayers.add(tempBest.cloneForReplay());
            LogHelper.e(TAG, "old best:", bestScore);
            LogHelper.e(TAG, "new best:", tempBest.getScore());
            bestScore = tempBest.getScore();
            bestPlayer = tempBest.cloneForReplay();
        }
    }

    /**
     * This function is called when all the players in the population are dead and a new generation needs to be made
     */
    public void naturalSelection()
    {
        categorize();//separate the population into species
        calculateFitness();//calculate the fitness of each player
        sortSpecies();//sort the species to be ranked in fitness order, best first
        if (massExtinctionEvent) {
            massExtinction();
            massExtinctionEvent = false;
        }
        cullSpecies();//kill off the bottom half of each species
        setBestPlayer();//save the best player of this gen
        killStaleSpecies();//remove species which haven't improved in the last 15(ish) generations
        killBadSpecies();//kill species which are so bad that they cant reproduce


        LogHelper.e(TAG,
                "generation",
                gen,
                "Number of mutations",
                innovationHistory.size(),
                "species: " + species.size(),
                "<<<<<<<<<<<<<<<<"
        );


        double averageSum = getAvgFitnessSum();
        List<Player> children = new ArrayList<>();//the next generation

        LogHelper.e(TAG, "Species:");
        for (Species specy : species) {//for each species
            LogHelper.e(TAG, "best unadjusted fitness:", specy.getBestFitness());
            for (int i = 0; i < specy.getPlayers().size(); i++) {
                LogHelper.e(TAG, "player " + i, "fitness: " + specy.getPlayers().get(i).getFitness(), "score " + specy.getPlayers().get(i).getScore(), ' ');
            }
            LogHelper.e(TAG, "");
            // Add champion without any mutation
            children.add(specy.getChamp().clone());

            // The number of children this species is allowed, note -1 is because the champ is already added
            int NoOfChildren = (int) Math.floor(specy.getAverageFitness() / averageSum * players.size()) - 1;
            for (int i = 0; i < NoOfChildren; i++) {
                // Get the calculated amount of children from this species
                children.add(specy.giveMeBaby(innovationHistory));
            }
        }

        while (children.size() < players.size()) {
            // If not enough babies (due to flooring the number of children to get a whole int)
            children.add(species.get(0).giveMeBaby(innovationHistory));//get babies from the best species
        }

        players.clear();
        players = new ArrayList<>(children); //set the children as the current population
        gen += 1;

        for (Player player : players) {//generate networks for each of the children
            player.getBrain().generateNetwork();
        }

        populationLife = 0;
    }

    /**
     * Separate population into species based on how similar they are to the leaders of each species in the previous gen
     */
    public void categorize()
    {
        for (Species s : species) {//empty species
            s.getPlayers().clear();
        }
        for (Player player : players) {//for each player
            boolean speciesFound = false;
            for (Species s : species) {//for each species
                if (s.sameSpecies(player.getBrain())) {//if the player is similar enough to be considered in the same species
                    s.addToSpecies(player);//add it to the species
                    speciesFound = true;
                    break;
                }
            }
            if (!speciesFound) {//if no species was similar enough then add a new species with this as its champion
                species.add(new Species(player));
            }
        }
    }

    /**
     * Calculates the fitness of all of the players
     */
    public void calculateFitness()
    {
        for (int i = 1; i < players.size(); i++) {
            players.get(i).calculateFitness();
        }
    }

    /**
     * Sorts the players within a species and the species by their fitnesses
     */
    public void sortSpecies()
    {
        // Sort the players within a species
        for (Species s : species) {
            s.sortSpecies();
        }

        // Sort the species by the fitness of its best player
        // Using selection sort like a loser
        List<Species> temp = new ArrayList<>();
        for (int i = 0; i < species.size(); i++) {
            double max = 0;
            int maxIndex = 0;
            for (int j = 0; j < species.size(); j++) {
                if (species.get(j).getBestFitness() > max) {
                    max = species.get(j).getBestFitness();
                    maxIndex = j;
                }
            }
            temp.add(species.get(maxIndex));
            species.remove(maxIndex);
            i--;
        }
        species = new ArrayList<>(temp);
    }

    /**
     * Kills all species which haven't improved in 15 generations
     */
    public void killStaleSpecies()
    {
        for (int i = 2; i < species.size(); i++) {
            if (species.get(i).getStaleness() >= 15) {
                species.remove(i);
                i--;
            }
        }
    }

    /**
     * If a species sucks so much that it wont even be allocated 1 child for the next generation then kill it now
     */
    public void killBadSpecies()
    {
        double averageSum = getAvgFitnessSum();

        for (int i = 1; i < species.size(); i++) {
            if (species.get(i).getAverageFitness() / averageSum * players.size() < 1) {//if wont be given a single child
                species.remove(i);//sad
                i--;
            }
        }
    }

    /**
     * Returns the sum of each species average fitness
     */
    public double getAvgFitnessSum()
    {
        double averageSum = 0;
        for (Species s : species) {
            averageSum += s.getAverageFitness();
        }
        return averageSum;
    }

    /**
     * Fill the bottom half of each species
     */
    public void cullSpecies()
    {
        for (Species s : species) {
            s.cull(); //kill bottom half
            s.fitnessSharing();//also while we're at it lets do fitness sharing
            s.setAverage();//reset averages because they will have changed
        }
    }


    public void massExtinction()
    {
        for (int i = 5; i < species.size(); i++) {
            species.remove(i);//sad
            i--;
        }
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    public Player getBestPlayer()
    {
        return bestPlayer;
    }

    public int getBestScore()
    {
        return bestScore;
    }

    public int getGen()
    {
        return gen;
    }

    public List<ConnectionHistory> getInnovationHistory()
    {
        return innovationHistory;
    }

    public List<Player> getGenPlayers()
    {
        return genPlayers;
    }

    public List<Species> getSpecies()
    {
        return species;
    }

    public boolean isMassExtinctionEvent()
    {
        return massExtinctionEvent;
    }

    public boolean isNewStage()
    {
        return newStage;
    }

    public int getPopulationLife()
    {
        return populationLife;
    }
}