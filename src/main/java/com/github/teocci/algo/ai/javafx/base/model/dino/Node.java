package com.github.teocci.algo.ai.javafx.base.model.dino;

import com.github.teocci.algo.ai.javafx.base.connections.ConnectionGene;
import com.github.teocci.algo.ai.javafx.base.model.dot.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class Node
{
    private int number;
    private float inputSum = 0; //current sum i.e. before activation
    private float outputValue = 0; //after activation function is applied

    private List<ConnectionGene> outputConnections = new ArrayList<>();

    private int layer = 0;

    private Vector2D drawPos = new PVector();


    public Node(int no)
    {
        number = no;
    }

    /**
     * The node sends its output to the inputs of the nodes its connected to
     */
    public void engage()
    {
        if (layer != 0) {//no sigmoid for the inputs and bias
            outputValue = sigmoid(inputSum);
        }

        for (int i = 0; i < outputConnections.size(); i++) {//for each connection
            if (outputConnections.get(i).enabled) {//dont do shit if not enabled
                outputConnections.get(i).toNode.inputSum += outputConnections.get(i).weight * outputValue;//add the weighted output to the sum of the inputs of whatever node this node is connected to
            }
        }
    }

    public float stepFunction(float x)
    {
        if (x < 0) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Sigmoid activation function
     */
    public float sigmoid(float x)
    {
        float y = 1 / (1 + Math.pow((float) Math.E, -4.9 * x));
        return y;
    }


    /**
     * Returns a copy of this node
     */
    public Node clone()
    {
        Node clone = new Node(number);
        clone.layer = layer;
        return clone;
    }

    /**
     * Returns whether this node connected to the parameter node
     * used when adding a new connection
     */
    public boolean isConnectedTo(Node node)
    {
        if (node.layer == layer) {//nodes in the same layer cannot be connected
            return false;
        }

        //you get it
        if (node.layer < layer) {
            for (int i = 0; i < node.outputConnections.size(); i++) {
                if (node.outputConnections.get(i).toNode == this) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < outputConnections.size(); i++) {
                if (outputConnections.get(i).toNode == node) {
                    return true;
                }
            }
        }

        return false;
    }
}
