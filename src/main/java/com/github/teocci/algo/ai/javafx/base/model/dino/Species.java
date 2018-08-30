package com.github.teocci.algo.ai.javafx.base.model.dino;

import com.github.teocci.algo.ai.javafx.base.connections.ConnectionHistory;

import java.util.ArrayList;
import java.util.List;

import static jdk.nashorn.internal.objects.NativeMath.random;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-30
 */
public class Species
{
    // Coefficients for testing compatibility
    private final double EXCESS_COEFFICIENT = 1;
    private final double WEIGHT_DIFF_COEFFICIENT = 0.5;
    private final double COMPATIBILITY_THRESHOLD = 3;

    private List<Player> players = new ArrayList<>();
    private double bestFitness = 0;

    private Player champ;

    private double averageFitness = 0;
    private int staleness = 0; // How many generations the species has gone without an improvement

    private Genome rep;

    public Species() {}


    /**
     * Constructor which takes in the player which belongs to the species
     */
    public Species(Player p)
    {
        players.add(p);
        //since it is the only one in the species it is by default the best
        bestFitness = p.getFitness();
        rep = p.getBrain().clone();
        champ = p.cloneForReplay();
    }

    /**
     * Returns whether the parameter genome is in this species
     */
    public boolean sameSpecies(Genome g)
    {
        double compatibility;
        double excessAndDisjoint = getExcessDisjoint(g, rep);//get the number of excess and disjoint genes between this player and the current species rep
        double averageWeightDiff = averageWeightDiff(g, rep);//get the average weight difference between matching genes


        float largeGenomeNormaliser = g.genes.size() - 20;
        if (largeGenomeNormaliser < 1) {
            largeGenomeNormaliser = 1;
        }

        // Compatibility formula
        compatibility = (EXCESS_COEFFICIENT * excessAndDisjoint / largeGenomeNormaliser) + (WEIGHT_DIFF_COEFFICIENT * averageWeightDiff);
        return (COMPATIBILITY_THRESHOLD > compatibility);
    }

    /**
     * Adds a player to the species
     */
    public void addToSpecies(Player p)
    {
        players.add(p);
    }

    /**
     * Returns the number of excess and disjoint genes between the 2 input genomes
     * i.e. returns the number of genes which don't match
     */
    public double getExcessDisjoint(Genome brain1, Genome brain2)
    {
        double matching = 0.0;
        for (int i = 0; i < brain1.genes.size(); i++) {
            for (int j = 0; j < brain2.genes.size(); j++) {
                if (brain1.genes.get(i).innovationNo == brain2.genes.get(j).innovationNo) {
                    matching++;
                    break;
                }
            }
        }
        return (brain1.genes.size() + brain2.genes.size() - 2 * (matching));//return no of excess and disjoint genes
    }

    /**
     * Returns the avereage weight difference between matching genes in the input genomes
     */
    public double averageWeightDiff(Genome brain1, Genome brain2)
    {
        if (brain1.genes.size() == 0 || brain2.genes.size() == 0) {
            return 0;
        }


        double matching = 0;
        double totalDiff = 0;
        for (int i = 0; i < brain1.genes.size(); i++) {
            for (int j = 0; j < brain2.genes.size(); j++) {
                if (brain1.genes.get(i).innovationNo == brain2.genes.get(j).innovationNo) {
                    matching++;
                    totalDiff += abs(brain1.genes.get(i).weight - brain2.genes.get(j).weight);
                    break;
                }
            }
        }
        if (matching == 0) {//divide by 0 error
            return 100;
        }
        return totalDiff / matching;
    }

    /**
     * Sorts the species by fitness
     */
    public void sortSpecies()
    {

        List<Player> temp = new ArrayList<Player>();

        //selection short
        for (int i = 0; i < players.size(); i++) {
            float max = 0;
            int maxIndex = 0;
            for (int j = 0; j < players.size(); j++) {
                if (players.get(j).fitness > max) {
                    max = players.get(j).fitness;
                    maxIndex = j;
                }
            }
            temp.add(players.get(maxIndex));
            players.remove(maxIndex);
            i--;
        }

        players = (ArrayList) temp.clone();
        if (players.size() == 0) {
            print("fucking");
            staleness = 200;
            return;
        }
        //if new best player
        if (players.get(0).fitness > bestFitness) {
            staleness = 0;
            bestFitness = players.get(0).fitness;
            rep = players.get(0).brain.clone();
            champ = players.get(0).cloneForReplay();
        } else {//if no new best player
            staleness++;
        }
    }

    public setAverage()
    {
        double sum = 0;
        for (Player player : players) {
            sum += player.fitness;
        }

        averageFitness = sum / players.size();
    }

    /**
     * Gets baby from the players in this species
     */
    public Player giveMeBaby(List<ConnectionHistory> innovationHistory)
    {
        Player baby;
        if (random(1) < 0.25) {//25% of the time there is no crossover and the child is simply a clone of a random(ish) player
            baby = selectPlayer().clone();
        } else {//75% of the time do crossover

            //get 2 random(ish) parents
            Player parent1 = selectPlayer();
            Player parent2 = selectPlayer();

            //the crossover function expects the highest fitness parent to be the object and the lowest as the argument
            if (parent1.fitness < parent2.fitness) {
                baby = parent2.crossover(parent1);
            } else {
                baby = parent1.crossover(parent2);
            }
        }
        baby.brain.mutate(innovationHistory);//mutate that baby brain
        return baby;
    }

    /**
     * Selects a player based on it fitness
     */
    public Player selectPlayer()
    {
        float fitnessSum = 0;
        for (int i = 0; i < players.size(); i++) {
            fitnessSum += players.get(i).fitness;
        }

        double rand = random(fitnessSum);
        double runningSum = 0;

        for (Player player : players) {
            runningSum += player.fitness;
            if (runningSum > rand) {
                return player;
            }
        }
        //unreachable code to make the parser happy
        return players.get(0);
    }

    /**
     * Kills off bottom half of the species
     */
    public void cull()
    {
        if (players.size() > 2) {
            for (int i = players.size() / 2; i < players.size(); i++) {
                players.remove(i);
                i--;
            }
        }
    }

    /**
     * To protect unique players, the fitnesses of each player is divided by the number of players in the species that that player belongs to
     */
    public void fitnessSharing()
    {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).fitness /= players.size();
        }
    }
}
