package graph;

import model.TaxonNode;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.HashMap;

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

    public void addEdge(MyEdge edge) {
        try {
            super.addEdge(edge.getSource(), edge.getTarget(), edge);
            super.setEdgeWeight(edge, edge.getWeight());
        } catch (IllegalArgumentException e) {
            System.err.println("Can't add edge:" + edge + ".\nMake sure the vertices have been added to the graph.");

        }
    }

    public SimpleGraph getGraph(){
        return this;
    }
    public HashMap<TaxonNode, MyVertex> getTaxonNodeToVertexMap() {
        return taxonNodeToVertexMap;
    }

    public HashMap<Integer, HashMap<Integer, MyEdge>> getNodeIdsToEdgesMap() {
        return nodeIdsToEdgesMap;
    }

    @Override
    public void setEdgeWeight(Object o, double v) {

    }
}
