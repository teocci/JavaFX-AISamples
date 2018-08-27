package com.github.teocci.algo.ai.javafx.base.connections;

import com.github.teocci.algo.ai.javafx.base.model.dino.Genome;
import com.github.teocci.algo.ai.javafx.base.model.dino.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class ConnectionHistory
{
    private int fromNode;
    private int toNode;
    private int innovationNumber;


    // The innovation Numbers from the connections of the genome which first had this mutation
    // This represents the genome and allows us to test if another genoeme is the same
    // This is before this connection was added
    private List<Integer> innovationNumbers;


    public ConnectionHistory(int from, int to, int inno, ArrayList<Integer> innovationNos)
    {
        fromNode = from;
        toNode = to;
        innovationNumber = inno;
        innovationNumbers = (ArrayList) innovationNos.clone();
    }

    /**
     * Returns whether the genome matches the original genome and the connection is between the same nodes
     */
    public boolean matches(Genome genome, Node from, Node to)
    {
        if (genome.genes.size() == innovationNumbers.size()) {
            if (from.number == fromNode && to.number == toNode) {
                // Next check if all the innovation numbers match from the genome
                for (int i = 0; i < genome.genes.size(); i++) {
                    if (!innovationNumbers.contains(genome.genes.get(i).innovationNo)) {
                        return false;
                    }
                }

                // If reached this far then the innovationNumbers match the genes innovation numbers and the connection is between the same nodes
                // So it does match
                return true;
            }
        }
        return false;
    }
}
