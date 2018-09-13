package com.github.teocci.algo.ai.javafx.base.model.dino;

import com.github.teocci.algo.ai.javafx.base.connections.ConnectionGene;
import com.github.teocci.algo.ai.javafx.base.connections.ConnectionHistory;
import com.github.teocci.algo.ai.javafx.base.controllers.dino.MainController;
import com.github.teocci.algo.ai.javafx.base.model.dot.Vector2D;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;
import com.github.teocci.algo.ai.javafx.base.utils.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class Genome
{
    private static final String TAG = LogHelper.makeLogTag(Genome.class);

    // A list of connections between nodes which represent the NN
    private List<ConnectionGene> genes = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();

    //A list of the nodes in the order that they need to be considered in the NN
    private List<Node> network = new ArrayList<>();

    private int inputs;
    private int outputs;
    private int layers = 2;
    private int nextNode = 0;
    private int biasNode;

    public Genome(int in, int out)
    {
        // Set input number and output number
        inputs = in;
        outputs = out;

        // Create input nodes
        for (int i = 0; i < inputs; i++) {
            nodes.add(new Node(i));
            nextNode++;
            nodes.get(i).setLayer(0);
        }

        // Create output nodes
        for (int i = 0; i < outputs; i++) {
            nodes.add(new Node(i + inputs));
            nodes.get(i + inputs).setLayer(1);
            nextNode++;
        }

        nodes.add(new Node(nextNode));//bias node
        biasNode = nextNode;
        nextNode++;
        nodes.get(biasNode).setLayer(0);
    }


    public Genome(int in, int out, boolean crossover)
    {
        //set input number and output number
        inputs = in;
        outputs = out;
    }

    /**
     * Returns the node with a matching number
     * sometimes the nodes will not be in order
     */
    public Node getNode(int nodeNumber)
    {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getNumber() == nodeNumber) {
                return nodes.get(i);
            }
        }
        return null;
    }

    /**
     * Adds connections going out of a node to that node so that it can acess the next node during feeding forward
     */
    public void connectNodes()
    {
        for (Node node : nodes) {//clear the connections
            node.getOutputConnections().clear();
        }

        for (ConnectionGene gene : genes) {//for each connectionGene
            gene.getFromNode().getOutputConnections().add(gene);//add it to node
        }
    }

    /**
     * Feeding in input values into the NN and returning output array
     */
    public double[] feedForward(double[] inputValues)
    {
        //set the outputs of the input nodes
        for (int i = 0; i < inputs; i++) {
            nodes.get(i).setOutputValue(inputValues[i]);
        }
        nodes.get(biasNode).setOutputValue(1);//output of bias is 1

        for (Node aNetwork : network) {//for each node in the network engage it(see node class for what this does)
            aNetwork.engage();
        }

        //the outputs are nodes[inputs] to nodes [inputs+outputs-1]
        double[] outs = new double[outputs];
        for (int i = 0; i < outputs; i++) {
            outs[i] = nodes.get(inputs + i).getOutputValue();
        }

        for (Node node : nodes) {//reset all the nodes for the next feed forward
            node.setInputSum(0);
        }

        return outs;
    }

    /**
     * Sets up the NN as a list of nodes in order to be engaged
     */
    public void generateNetwork()
    {
        connectNodes();
        network = new ArrayList<>();
        //for each layer add the node in that layer, since layers cannot connect to themselves there is no need to order the nodes within a layer

        for (int l = 0; l < layers; l++) {//for each layer
            for (Node node : nodes) {//for each node
                if (node.getLayer() == l) {//if that node is in that layer
                    network.add(node);
                }
            }
        }
    }

    /**
     * Mutates the NN by adding a new node
     * it does this by picking a random connection and disabling it then 2 new connections are added
     * 1 between the input node of the disabled connection and the new node
     * and the other between the new node and the output of the disabled connection
     */
    public void addNode(List<ConnectionHistory> innovationHistory)
    {
        //pick a random connection to create a node between
        if (genes.size() == 0) {
            addConnection(innovationHistory);
            return;
        }
        int randomConnection = Random.uniform(genes.size());

        while (genes.get(randomConnection).getFromNode() == nodes.get(biasNode) && genes.size() != 1) {
            //dont disconnect bias
            randomConnection = Random.uniform(genes.size());
        }

        genes.get(randomConnection).setEnabled(false);//disable it

        int newNodeNo = nextNode;
        nodes.add(new Node(newNodeNo));
        nextNode++;
        //add a new connection to the new node with a weight of 1
        int connectionInnovationNumber = getInnovationNumber(
                innovationHistory,
                genes.get(randomConnection).getFromNode(),
                getNode(newNodeNo)
        );
        genes.add(new ConnectionGene(genes.get(randomConnection).getFromNode(),
                getNode(newNodeNo),
                1,
                connectionInnovationNumber)
        );


        connectionInnovationNumber = getInnovationNumber(innovationHistory,
                getNode(newNodeNo),
                genes.get(randomConnection).getToNode()
        );
        //add a new connection from the new node with a weight the same as the disabled connection
        genes.add(new ConnectionGene(getNode(newNodeNo),
                genes.get(randomConnection).getToNode(),
                genes.get(randomConnection).getWeight(),
                connectionInnovationNumber)
        );
        getNode(newNodeNo).setLayer(genes.get(randomConnection).getFromNode().getLayer() + 1);


        connectionInnovationNumber = getInnovationNumber(innovationHistory, nodes.get(biasNode), getNode(newNodeNo));
        //connect the bias to the new node with a weight of 0
        genes.add(new ConnectionGene(nodes.get(biasNode), getNode(newNodeNo), 0, connectionInnovationNumber));

        // If the layer of  new node is equal to the layer of the output node of the old connection
        // then a new layer needs to be created more accurately.
        // The layer numbers of all layers equal to (or greater than) this new node need to be incremented
        if (getNode(newNodeNo).getLayer() == genes.get(randomConnection).getToNode().getLayer()) {
            for (int i = 0; i < nodes.size() - 1; i++) {//dont include this newest node
                if (nodes.get(i).getLayer() >= getNode(newNodeNo).getLayer()) {
                    nodes.get(i).increaseLayer();
                }
            }
            layers++;
        }
        connectNodes();
    }

    /**
     * Adds a connection between 2 nodes which aren't currently connected
     */
    public void addConnection(List<ConnectionHistory> innovationHistory)
    {
        // Cannot add a connection to a fully connected network
        if (fullyConnected()) {
            LogHelper.e(TAG, "connection failed");
            return;
        }

        //get random nodes
        int randomNode1 = Random.uniform(nodes.size());
        int randomNode2 = Random.uniform(nodes.size());
        while (randomConnectionNodesAreShit(randomNode1, randomNode2)) {//while the random nodes are no good
            //get new ones
            randomNode1 = Random.uniform(nodes.size());
            randomNode2 = Random.uniform(nodes.size());
        }
        int temp;
        if (nodes.get(randomNode1).getLayer() > nodes.get(randomNode2).getLayer()) {//if the first random node is after the second then switch
            temp = randomNode2;
            randomNode2 = randomNode1;
            randomNode1 = temp;
        }

        //get the innovation number of the connection
        //this will be a new number if no identical genome has mutated in the same way
        int connectionInnovationNumber = getInnovationNumber(innovationHistory, nodes.get(randomNode1), nodes.get(randomNode2));
        //add the connection with a random array

        genes.add(new ConnectionGene(
                nodes.get(randomNode1),
                nodes.get(randomNode2),
                Random.uniform(-1, 1),
                connectionInnovationNumber)
        );//changed this so if error here
        connectNodes();
    }

    public boolean randomConnectionNodesAreShit(int r1, int r2)
    {
        if (nodes.get(r1).getLayer() == nodes.get(r2).getLayer()) return true; // if the nodes are in the same layer
        //if the nodes are already connected
        return nodes.get(r1).isConnectedTo(nodes.get(r2));

    }


    //returns the innovation number for the new mutation
    //if this mutation has never been seen before then it will be given a new unique innovation number
    //if this mutation matches a previous mutation then it will be given the same innovation number as the previous one
    public int getInnovationNumber(List<ConnectionHistory> innovationHistory, Node from, Node to)
    {
        boolean isNew = true;
        int connectionInnovationNumber = MainController.getInstance().getNextConnectionNo();
        for (ConnectionHistory anInnovationHistory : innovationHistory) {//for each previous mutation
            if (anInnovationHistory.matches(this, from, to)) {//if match found
                isNew = false;//its not a new mutation
                connectionInnovationNumber = anInnovationHistory.getInnovationNumber(); //set the innovation number as the innovation number of the match
                break;
            }
        }

        // If a mutation is new then create an arrayList of integers representing the current state of the genome
        if (isNew) {
            List<Integer> innoNumbers = new ArrayList<>();
            for (ConnectionGene gene : genes) {
                // Set the innovation numbers
                innoNumbers.add(gene.getInnovationNo());
            }

            // Then add this mutation to the innovationHistory
            innovationHistory.add(new ConnectionHistory(
                    from.getNumber(),
                    to.getNumber(),
                    connectionInnovationNumber,
                    innoNumbers)
            );
            MainController.getInstance().increaseNextConnectionNo();
        }
        return connectionInnovationNumber;
    }

    /**
     * Returns whether the network is fully connected or not
     */
    public boolean fullyConnected()
    {
        int maxConnections = 0;
        int[] nodesInLayers = new int[layers];//array which stored the amount of nodes in each layer

        //populate array
        for (int i = 0; i < nodes.size(); i++) {
            nodesInLayers[nodes.get(i).getLayer()] += 1;
        }

        //for each layer the maximum amount of connections is the number in this layer * the number of nodes infront of it
        //so lets add the max for each layer together and then we will get the maximum amount of connections in the network
        for (int i = 0; i < layers - 1; i++) {
            int nodesInFront = 0;
            for (int j = i + 1; j < layers; j++) {//for each layer infront of this layer
                nodesInFront += nodesInLayers[j];//add up nodes
            }

            maxConnections += nodesInLayers[i] * nodesInFront;
        }

        //if the number of connections is equal to the max number of connections possible then it is full
        return maxConnections == genes.size();
    }


    /**
     * Mutates the genome
     */
    public void mutate(List<ConnectionHistory> innovationHistory)
    {
        if (genes.size() == 0) {
            addConnection(innovationHistory);
        }

        double rand1 = Random.uniform();
        if (rand1 < 0.8) { // 80% of the time mutate weights
            for (int i = 0; i < genes.size(); i++) {
                genes.get(i).mutateWeight();
            }
        }
        //5% of the time add a new connection
        double rand2 = Random.uniform();
        if (rand2 < 0.08) {
            addConnection(innovationHistory);
        }


        //1% of the time add a node
        double rand3 = Random.uniform();
        if (rand3 < 0.02) {
            addNode(innovationHistory);
        }
    }

    /**
     * Called when this Genome is better that the other parent
     */
    public Genome crossover(Genome parent2)
    {
        Genome child = new Genome(inputs, outputs, true);

        child.genes.clear();
        child.nodes.clear();
        child.layers = layers;
        child.nextNode = nextNode;
        child.biasNode = biasNode;

        List<ConnectionGene> childGenes = new ArrayList<>();//list of genes to be inherrited form the parents
        List<Boolean> isEnabled = new ArrayList<>();

        // All inherited genes
        for (int i = 0; i < genes.size(); i++) {
            boolean setEnabled = true;//is this node in the chlid going to be enabled

            int parent2gene = matchingGene(parent2, genes.get(i).getInnovationNo());
            if (parent2gene != -1) {//if the genes match
                if (!genes.get(i).isEnabled() || !parent2.genes.get(parent2gene).isEnabled()) {//if either of the matching genes are disabled
                    if (Random.uniform() < 0.75) {//75% of the time disable the childes gene
                        setEnabled = false;
                    }
                }
                double rand = Random.uniform();
                if (rand < 0.5) {
                    childGenes.add(genes.get(i));
                    //get gene from this fucker
                } else {
                    //get gene from parent2
                    childGenes.add(parent2.genes.get(parent2gene));
                }
            } else {//disjoint or excess gene
                childGenes.add(genes.get(i));
                setEnabled = genes.get(i).isEnabled();
            }
            isEnabled.add(setEnabled);
        }


        // Since all excess and disjoint genes are inherited from the more fit parent (this Genome)
        // the childes structure is no different from this parent | with exception of dormant connections being
        // enabled but this wont effect nodes. So, all the nodes can be inherited from this parent
        for (int i = 0; i < nodes.size(); i++) {
            child.nodes.add(nodes.get(i).clone());
        }

        //clone all the connections so that they connect the childes new nodes
        for (int i = 0; i < childGenes.size(); i++) {
            child.genes.add(childGenes.get(i).clone(
                    child.getNode(childGenes.get(i).getFromNode().getNumber()),
                    child.getNode(childGenes.get(i).getToNode().getNumber()))
            );
            child.genes.get(i).setEnabled(isEnabled.get(i));
        }

        child.connectNodes();
        return child;
    }

    /**
     * Returns whether or not there is a gene matching the input innovation number  in the input genome
     */
    public int matchingGene(Genome parent2, int innovationNumber)
    {
        for (int i = 0; i < parent2.genes.size(); i++) {
            if (parent2.genes.get(i).getInnovationNo() == innovationNumber) {
                return i;
            }
        }
        return -1; //no matching gene found
    }

    /**
     * Prints out info about the genome to the console
     */
    public void printGenome()
    {
        LogHelper.e(TAG, "Print genome  layers:", layers);
        LogHelper.e(TAG, "bias node: " + biasNode);
        LogHelper.e(TAG, "nodes");
        for (int i = 0; i < nodes.size(); i++) {
//            print(nodes.get(i).number + ",");
        }
        LogHelper.e(TAG, "Genes");
        for (ConnectionGene gene : genes) {
            LogHelper.e(TAG, "gene " + gene.getInnovationNo(),
                    "From node " + gene.getFromNode().getNumber(),
                    "To node " + gene.getToNode().getNumber(),
                    "is enabled " + gene.isEnabled(),
                    "from layer " + gene.getFromNode().getLayer(),
                    "to layer " + gene.getToNode().getLayer(),
                    "weight: " + gene.getWeight()
            );
        }
    }

    /**
     * Returns a copy of this genome
     */
    public Genome clone()
    {
        Genome clone = new Genome(inputs, outputs, true);

        for (int i = 0; i < nodes.size(); i++) {//copy nodes
            clone.nodes.add(nodes.get(i).clone());
        }

        // Copy all the connections so that they connect the clone new nodes
        for (int i = 0; i < genes.size(); i++) {//copy genes
            clone.genes.add(genes.get(i).clone(
                    clone.getNode(genes.get(i).getFromNode().getNumber()),
                    clone.getNode(genes.get(i).getToNode().getNumber()))
            );
        }

        clone.layers = layers;
        clone.nextNode = nextNode;
        clone.biasNode = biasNode;
        clone.connectNodes();

        return clone;
    }

    /**
     * Draw the genome on the screen
     */
    public void drawGenome(int startX, int startY, int w, int h)
    {
        // I know its ugly but it works (and is not that important) so I'm not going to mess with it
        List<List<Node>> allNodes = new ArrayList<>();
        List<Vector2D> nodePoses = new ArrayList<>();
        List<Integer> nodeNumbers = new ArrayList<>();

        // Get the positions on the screen that each node is supposed to be in


        //split the nodes into layers
        for (int i = 0; i < layers; i++) {
            ArrayList<Node> temp = new ArrayList<Node>();
            for (Node node : nodes) {//for each node
                if (node.getLayer() == i) {//check if it is in this layer
                    temp.add(node); //add it to this layer
                }
            }
            allNodes.add(temp);//add this layer to all nodes
        }

        // Add the position of the node on the screen to the node posses ArrayList for each layer
        for (int i = 0; i < layers; i++) {
//            fill(255, 0, 0);
            double x = startX + (double) ((i) * w) / (double) (layers - 1);
            for (int j = 0; j < allNodes.get(i).size(); j++) {//for the position in the layer
                double y = startY + ((j + 1.0) * h) / (allNodes.get(i).size() + 1.0);
                nodePoses.add(new Vector2D(x, y));
                nodeNumbers.add(allNodes.get(i).get(j).getNumber());
                if (i == layers - 1) {
                    LogHelper.e(TAG, i, j, x, y);
                }
            }
        }

        // Draw connections
//        stroke(0);
//        strokeWeight(2);
        for (int i = 0; i < genes.size(); i++) {
            if (genes.get(i).isEnabled()) {
//                stroke(0);
            } else {
//                stroke(100);
            }
            Vector2D from = nodePoses.get(nodeNumbers.indexOf(genes.get(i).getFromNode().getNumber()));
            Vector2D to = nodePoses.get(nodeNumbers.indexOf(genes.get(i).getToNode().getNumber()));

            if (genes.get(i).getWeight() > 0) {
//                stroke(255, 0, 0);
            } else {
//                stroke(0, 0, 255);
            }
//            strokeWeight(map(abs(genes.get(i).getWeight()), 0, 1, 0, 5));
//            line(from.x, from.y, to.x, to.y);
        }

        // Draw nodes last so they appear on top of the connection lines
        for (int i = 0; i < nodePoses.size(); i++) {
//            fill(255);
//            stroke(0);
//            strokeWeight(1);
//            ellipse(nodePoses.get(i).x, nodePoses.get(i).y, 20, 20);
//            textSize(10);
//            fill(0);
//            textAlign(CENTER, CENTER);
//
//            text(nodeNumbers.get(i), nodePoses.get(i).getX(), nodePoses.get(i).getY());
        }
    }

    public List<ConnectionGene> getGenes()
    {
        return genes;
    }

    public List<Node> getNodes()
    {
        return nodes;
    }

    public List<Node> getNetwork()
    {
        return network;
    }

    public int getInputs()
    {
        return inputs;
    }

    public int getOutputs()
    {
        return outputs;
    }

    public int getLayers()
    {
        return layers;
    }

    public int getNextNode()
    {
        return nextNode;
    }

    public int getBiasNode()
    {
        return biasNode;
    }
}
