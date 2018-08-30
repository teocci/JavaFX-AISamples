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

    private double inputSum = 0; //current sum i.e. before activation
    private double outputValue = 0; //after activation function is applied

    private List<ConnectionGene> outputConnections = new ArrayList<>();

    private int layer = 0;

    private Vector2D drawPos = new Vector2D();

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

        for (ConnectionGene outputConnection : outputConnections) {
            if (outputConnection.isEnabled()) {//don't do shit if not enabled
                //add the weighted output to the sum of the inputs of whatever node this node is connected to
                outputConnection.getToNode().inputSum += outputConnection.getWeight() * outputValue;
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
    public double sigmoid(double x)
    {
        return 1 / (1 + Math.pow(Math.E, -4.9 * x));
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


    public void increaseLayer()
    {
        this.layer++;
    }


    public void setNumber(int number)
    {
        this.number = number;
    }

    public void setInputSum(double inputSum)
    {
        this.inputSum = inputSum;
    }

    public void setOutputValue(double outputValue)
    {
        this.outputValue = outputValue;
    }

    public void setOutputConnections(List<ConnectionGene> outputConnections)
    {
        this.outputConnections = outputConnections;
    }

    public void setLayer(int layer)
    {
        this.layer = layer;
    }

    public void setDrawPos(Vector2D drawPos)
    {
        this.drawPos = drawPos;
    }


    public int getNumber()
    {
        return number;
    }

    public double getInputSum()
    {
        return inputSum;
    }

    public double getOutputValue()
    {
        return outputValue;
    }

    public List<ConnectionGene> getOutputConnections()
    {
        return outputConnections;
    }

    public int getLayer()
    {
        return layer;
    }

    public Vector2D getDrawPos()
    {
        return drawPos;
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
                if (node.outputConnections.get(i).getToNode() == this) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < outputConnections.size(); i++) {
                if (outputConnections.get(i).getToNode() == node) {
                    return true;
                }
            }
        }

        return false;
    }
}
