package com.github.teocci.algo.ai.javafx.base.connections;

import com.github.teocci.algo.ai.javafx.base.model.dino.Node;
import com.github.teocci.algo.ai.javafx.base.utils.Random;

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

    private double weight;
    private boolean enabled = true;

    // Each connection has a innovation number to compare genomes
    private int innovationNo;

    public ConnectionGene(Node from, Node to, double w, int inno)
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
        double rand = Random.uniform();
        // 10% of the time completely change the weight
        if (rand < 0.1) {
            weight = Random.uniform(-1, 1);
        } else { // Otherwise slightly change it
            weight += Random.gaussian() / 50;
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

    public void setFromNode(Node fromNode)
    {
        this.fromNode = fromNode;
    }

    public void setToNode(Node toNode)
    {
        this.toNode = toNode;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setInnovationNo(int innovationNo)
    {
        this.innovationNo = innovationNo;
    }


    public Node getFromNode()
    {
        return fromNode;
    }

    public Node getToNode()
    {
        return toNode;
    }

    public double getWeight()
    {
        return weight;
    }

    public int getInnovationNo()
    {
        return innovationNo;
    }


    public boolean isEnabled()
    {
        return enabled;
    }
}
