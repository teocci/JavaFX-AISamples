package com.github.teocci.algo.ai.javafx.base.model.dino;

import com.github.teocci.algo.ai.javafx.base.connections.ConnectionHistory;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import com.github.teocci.algo.ai.javafx.base.utils.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-30
 */
public class Species
{
    private static final String TAG = LogHelper.makeLogTag(Species.class);

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


        double largeGenomeNormaliser = g.getGenes().size() - 20;
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
        for (int i = 0; i < brain1.getGenes().size(); i++) {
            for (int j = 0; j < brain2.getGenes().size(); j++) {
                if (brain1.getGenes().get(i).getInnovationNo() == brain2.getGenes().get(j).getInnovationNo()) {
                    matching++;
                    break;
                }
            }
        }
        return (brain1.getGenes().size() + brain2.getGenes().size() - 2 * (matching));//return no of excess and disjoint genes
    }

    /**
     * Returns the avereage weight difference between matching genes in the input genomes
     */
    public double averageWeightDiff(Genome brain1, Genome brain2)
    {
        if (brain1.getGenes().size() == 0 || brain2.getGenes().size() == 0) {
            return 0;
        }

        double matching = 0;
        double totalDiff = 0;
        for (int i = 0; i < brain1.getGenes().size(); i++) {
            for (int j = 0; j < brain2.getGenes().size(); j++) {
                if (brain1.getGenes().get(i).getInnovationNo() == brain2.getGenes().get(j).getInnovationNo()) {
                    matching++;
                    totalDiff += Math.abs(brain1.getGenes().get(i).getWeight() - brain2.getGenes().get(j).getWeight());
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
        List<Player> temp = new ArrayList<>();

        //selection short
        for (int i = 0; i < players.size(); i++) {
            double max = 0;
            int maxIndex = 0;
            for (int j = 0; j < players.size(); j++) {
                if (players.get(j).getFitness() > max) {
                    max = players.get(j).getFitness();
                    maxIndex = j;
                }
            }
            temp.add(players.get(maxIndex));
            players.remove(maxIndex);
            i--;
        }

        players = new ArrayList<>(temp);
        if (players.size() == 0) {
            LogHelper.e(TAG, "fucking");
            staleness = 200;
            return;
        }
        //if new best player
        if (players.get(0).getFitness() > bestFitness) {
            staleness = 0;
            bestFitness = players.get(0).getFitness();
            rep = players.get(0).getBrain().clone();
            champ = players.get(0).cloneForReplay();
        } else {//if no new best player
            staleness++;
        }
    }

    public void setAverage()
    {
        double sum = 0;
        for (Player player : players) {
            sum += player.getFitness();
        }

        averageFitness = sum / players.size();
    }

    /**
     * Gets baby from the players in this species
     */
    public Player giveMeBaby(List<ConnectionHistory> innovationHistory)
    {
        Player baby;
        if (Random.uniform() < 0.25) {//25% of the time there is no crossover and the child is simply a clone of a random(ish) player
            baby = selectPlayer().clone();
        } else {//75% of the time do crossover

            //get 2 random(ish) parents
            Player parent1 = selectPlayer();
            Player parent2 = selectPlayer();

            //the crossover function expects the highest fitness parent to be the object and the lowest as the argument
            if (parent1.getFitness() < parent2.getFitness()) {
                baby = parent2.crossover(parent1);
            } else {
                baby = parent1.crossover(parent2);
            }
        }

        //mutate that baby brain
        baby.getBrain().mutate(innovationHistory);

        return baby;
    }

    /**
     * Selects a player based on it fitness
     */
    public Player selectPlayer()
    {
        double fitnessSum = 0;
        for (Player player1 : players) {
            fitnessSum += player1.getFitness();
        }

        double rand = Random.uniform(0, fitnessSum);
        double runningSum = 0;

        for (Player player : players) {
            runningSum += player.getFitness();
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
            double fitness = players.get(i).getFitness();
            fitness /= players.size();
            players.get(i).setFiness(fitness);
        }
    }

    public void setPlayers(List<Player> players)
    {
        this.players = players;
    }

    public void setBestFitness(double bestFitness)
    {
        this.bestFitness = bestFitness;
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    public Player getChamp()
    {
        return champ;
    }

    public double getBestFitness()
    {
        return bestFitness;
    }

    public double getAverageFitness()
    {
        return averageFitness;
    }

    public int getStaleness()
    {
        return staleness;
    }
}
