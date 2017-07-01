package graph;

import analysis.SampleComparison;
import model.Sample;
import model.TaxonNode;
import org.apache.commons.math3.linear.RealMatrix;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by julian on 10.06.17.
 */
public class MyGraph extends SimpleWeightedGraph {
    private HashMap<TaxonNode, MyVertex> taxonNodeToVertexMap;
    private HashMap<Integer, HashMap<Integer, MyEdge>> nodeIdsToEdgesMap;

    /**
     * Since edges are always going to be of type MyEdge, we only need this constructor
     */
    public MyGraph() {
        super(MyEdge.class);
        taxonNodeToVertexMap = new HashMap<>();
        nodeIdsToEdgesMap = new HashMap<>();

    }

    @Override
    public boolean addVertex(Object o) {
        return super.addVertex(o);
    }


    public void addMyEdge(MyEdge edge, double weight) {
        try {
            super.addEdge(edge.getSource(), edge.getTarget(), edge);
            super.setEdgeWeight(edge, weight);
        } catch (IllegalArgumentException e) {
            System.err.println("Can't add edge:" + edge + ".\nMake sure the vertices have been added to the graph.");

        }
    }

    /**
     * Filters the taxa contained in the list of samples. Returns a list of taxa that lie below/above the given
     * lower/upper correlation thresholds and below the given p-Value threshold
     *
     * @param samples
     * @param lowerCorrelationThreshold
     * @param upperCorrelationThreshold
     * @param pValueThreshold
     */
    public void filterTaxa(List<Sample> samples, double lowerCorrelationThreshold, double upperCorrelationThreshold, double pValueThreshold) {
        //Get the unfiltered List of all taxons contained in either sample1 or sample2 and sort it by node id
        LinkedList<TaxonNode> unfilteredTaxonList = SampleComparison.getUnifiedTaxonList(samples);

        //Counts the visible edges of each node - is initially set to n-1 for every node, decremented when edge is hidden
        int[] visibleEdgeCounts = new int[unfilteredTaxonList.size()];
        Arrays.fill(visibleEdgeCounts, unfilteredTaxonList.size() - 1);

        //Get correlation matrix and p-value matrix
        RealMatrix correlationMatrix = SampleComparison.getCorrelationMatrixOfSamples(samples);
        RealMatrix correlationPValues = SampleComparison.getCorrelationPValuesOfSamples(samples);

        //Compare every node with every other node
        for (int i = 0; i < unfilteredTaxonList.size(); i++) {
            for (int j = 0; j < i; j++) {
                //Access MyEdge object via the Hashmap (we need the node ids for this)
                int idOfFirstNode = unfilteredTaxonList.get(i).getTaxonId();
                int idOfSecondNode = unfilteredTaxonList.get(j).getTaxonId();
                MyEdge currentEdge = getNodeIdsToEdgesMap().get(idOfFirstNode).get(idOfSecondNode);
                //Test if edge between the nodes should be hidden
                if (correlationMatrix.getEntry(i, j) < upperCorrelationThreshold ||
                        correlationMatrix.getEntry(i, j) > lowerCorrelationThreshold ||
                        correlationPValues.getEntry(i, j) > pValueThreshold) {
                    //Hide it
                    currentEdge.hideEdge();
                    //Decrement edgeCount of both nodes
                    visibleEdgeCounts[i]--;
                    visibleEdgeCounts[j]--;
                } else {
                    //Show it
                    currentEdge.showEdge();
                }
            }
        }
        //Hide all vertices that don't have visible edges anymore
        for (int i = 0; i < visibleEdgeCounts.length; i++) {
            if(visibleEdgeCounts[i] == 0)
                taxonNodeToVertexMap.get(unfilteredTaxonList.get(i)).hideVertex();
        }
    }

    /*
  The following 3 methods are basically overloaded filters with default params for 2 of the three doubles
   */
    public void filterSamplesByPValue(List<Sample> samples, double pValueThreshold) {
        filterTaxa(samples, 1, -1, pValueThreshold);
    }

    public void filterSamplesByMinimalCorrelation(List<Sample> samples, double upperCorrelationThreshold) {
        filterTaxa(samples, 1, upperCorrelationThreshold, 1);
    }

    public void filterSamplesByMaximalCorrelation(List<Sample> samples, double lowerCorrelationThreshold) {
        filterTaxa(samples, lowerCorrelationThreshold, -1, 1);
    }


    public SimpleGraph getGraph() {
        return this;
    }

    public HashMap<TaxonNode, MyVertex> getTaxonNodeToVertexMap() {
        return taxonNodeToVertexMap;
    }

    public HashMap<Integer, HashMap<Integer, MyEdge>> getNodeIdsToEdgesMap() {
        return nodeIdsToEdgesMap;
    }

}
