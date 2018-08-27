package com.github.teocci.algo.ai.javafx.base.connections;

import com.github.teocci.algo.ai.javafx.base.model.dino.Node;

/**
 * A connection between 2 nodes
 * <p>
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class ConnectionGene
{
    private Node fromNode;
    private Node toNode;

    private float weight;
    private boolean enabled = true;

    // Each connection has a innovation number to compare genomes
    private int innovationNo;


    public ConnectionGene(Node from, Node to, float w, int inno)
    {
        fromNode = from;
        toNode = to;
        weight = w;
        innovationNo = inno;
    }


    /**
     * Changes the weight
     */
    public void mutateWeight()
    {
        float rand = random(1);
        // 10% of the time completely change the weight
        if (rand < 0.1) {
            weight = random(-1, 1);
        } else { // Otherwise slightly change it
            weight += randomGaussian() / 50;
            // Keep weight between bounds
            if (weight > 1) {
                weight = 1;
            }
            if (weight < -1) {
                weight = -1;
            }
        }
    }

    /**
     * Returns a copy of this connectionGene
     */
    public ConnectionGene clone(Node from, Node to)
    {
        ConnectionGene clone = new ConnectionGene(from, to, weight, innovationNo);
        clone.enabled = enabled;

        return clone;
    }
}
