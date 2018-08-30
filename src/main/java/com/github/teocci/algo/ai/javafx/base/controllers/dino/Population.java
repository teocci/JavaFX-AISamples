package com.github.teocci.algo.ai.javafx.base.controllers.dino;

import com.github.teocci.algo.ai.javafx.base.connections.ConnectionHistory;
import com.github.teocci.algo.ai.javafx.base.model.dino.Player;
import com.github.teocci.algo.ai.javafx.base.model.dino.Species;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-30
 */
public class Population
{
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
            players.add(new Player());
            players.get(i).getBrain().generateNetwork();
            players.get(i).getBrain().mutate(innovationHistory);
        }
    }


    /**
     * Update all the players which are alive
     */
    public void updateAlive()
    {
        populationLife++;
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).dead) {
                players.get(i).look();//get inputs for brain
                players.get(i).think();//use outputs from neural network
                players.get(i).update();//move the player according to the outputs from the neural network
                if (!showNothing) {
                    players.get(i).show();
                }
            }
        }
    }

    /**
     * Returns true if all the players are dead
     */
    public boolean done()
    {
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).dead) {
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
        Player tempBest = species.get(0).players.get(0);
        tempBest.gen = gen;


        // If best this gen is better than the global best score then set the global best as the best this gen

        if (tempBest.score > bestScore) {
            genPlayers.add(tempBest.cloneForReplay());
            println("old best:", bestScore);
            println("new best:", tempBest.score);
            bestScore = tempBest.score;
            bestPlayer = tempBest.cloneForReplay();
        }
    }

    /**
     * This function is called when all the players in the population are dead and a new generation needs to be made
     */
    public void naturalSelection()
    {
        categorize();//seperate the population into species
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


        println("generation", gen, "Number of mutations", innovationHistory.size(), "species: " + species.size(), "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");


        float averageSum = getAvgFitnessSum();
        List<Player> children = new ArrayList<>();//the next generation
        println("Species:");
        for (int j = 0; j < species.size(); j++) {//for each species

            println("best unadjusted fitness:", species.get(j).bestFitness);
            for (int i = 0; i < species.get(j).players.size(); i++) {
                print("player " + i, "fitness: " + species.get(j).players.get(i).fitness, "score " + species.get(j).players.get(i).score, ' ');
            }
            println();
            children.add(species.get(j).champ.clone());//add champion without any mutation

            int NoOfChildren = floor(species.get(j).averageFitness / averageSum * players.size()) - 1;//the number of children this species is allowed, note -1 is because the champ is already added
            for (int i = 0; i < NoOfChildren; i++) {//get the calculated amount of children from this species
                children.add(species.get(j).giveMeBaby(innovationHistory));
            }
        }

        while (children.size() < players.size()) {//if not enough babies (due to flooring the number of children to get a whole int)
            children.add(species.get(0).giveMeBaby(innovationHistory));//get babies from the best species
        }
        players.clear();
        players = (ArrayList) children.clone(); //set the children as the current population
        gen += 1;
        for (int i = 0; i < players.size(); i++) {//generate networks for each of the children
            players.get(i).brain.generateNetwork();
        }

        populationLife = 0;
    }

    /**
     * Separate population into species based on how similar they are to the leaders of each species in the previous gen
     */
    public void categorize()
    {
        for (Species s : species) {//empty species
            s.players.clear();
        }
        for (int i = 0; i < players.size(); i++) {//for each player
            boolean speciesFound = false;
            for (Species s : species) {//for each species
                if (s.sameSpecies(players.get(i).brain)) {//if the player is similar enough to be considered in the same species
                    s.addToSpecies(players.get(i));//add it to the species
                    speciesFound = true;
                    break;
                }
            }
            if (!speciesFound) {//if no species was similar enough then add a new species with this as its champion
                species.add(new Species(players.get(i)));
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
        ArrayList<Species> temp = new ArrayList<Species>();
        for (int i = 0; i < species.size(); i++) {
            float max = 0;
            int maxIndex = 0;
            for (int j = 0; j < species.size(); j++) {
                if (species.get(j).bestFitness > max) {
                    max = species.get(j).bestFitness;
                    maxIndex = j;
                }
            }
            temp.add(species.get(maxIndex));
            species.remove(maxIndex);
            i--;
        }
        species = (ArrayList) temp.clone();
    }

    /**
     * Kills all species which haven't improved in 15 generations
     */
    public void killStaleSpecies()
    {
        for (int i = 2; i < species.size(); i++) {
            if (species.get(i).staleness >= 15) {
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
        float averageSum = getAvgFitnessSum();

        for (int i = 1; i < species.size(); i++) {
            if (species.get(i).averageFitness / averageSum * players.size() < 1) {//if wont be given a single child
                species.remove(i);//sad
                i--;
            }
        }
    }

    /**
     * Returns the sum of each species average fitness
     */
    public float getAvgFitnessSum()
    {
        float averageSum = 0;
        for (Species s : species) {
            averageSum += s.averageFitness;
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