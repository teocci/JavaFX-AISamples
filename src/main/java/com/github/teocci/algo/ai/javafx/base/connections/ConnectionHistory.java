package com.github.teocci.algo.ai.javafx.base.connections;

import com.github.teocci.algo.ai.javafx.base.model.dino.Genome;
import com.github.teocci.algo.ai.javafx.base.model.dino.Node;
import com.github.teocci.algo.ai.javafx.base.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Aug-27
 */
public class ConnectionHistory
{
    private static final String TAG = LogHelper.makeLogTag(Genome.class);

    private int fromNode;
    private int toNode;

    private int innovationNumber;


    // The innovation Numbers from the connections of the genome which first had this mutation
    // This represents the genome and allows us to test if another genome is the same
    // This is before this connection was added
    private List<Integer> innovationNumbers;


    public ConnectionHistory(int from, int to, int inno, List<Integer> innovationNos)
    {
        fromNode = from;
        toNode = to;
        innovationNumber = inno;
        innovationNumbers = new ArrayList<>(innovationNos);
    }

    /**
     * Returns whether the genome matches the original genome and the connection is between the same nodes
     */
    public boolean matches(Genome genome, Node from, Node to)
    {
        if (genome.getGenes().size() == innovationNumbers.size()) {
            if (from.getNumber() == fromNode && to.getNumber() == toNode) {
                // Next check if all the innovation numbers match from the genome
                for (int i = 0; i < genome.getGenes().size(); i++) {
                    if (!innovationNumbers.contains(genome.getGenes().get(i).getInnovationNo())) {
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

    public int getInnovationNumber()
    {
        return innovationNumber;
    }
}
