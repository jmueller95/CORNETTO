package graph;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * Created by julian on 10.06.17.
 */
public class MyGraph extends SimpleWeightedGraph {

    /**
     * Since edges are always going to be of type MyEdge, we only need this constructor
     */
    public MyGraph() {
        super(MyEdge.class);

    }

    @Override
    public boolean addVertex(Object o) {
        return super.addVertex(o);
    }

    public void addEdge(MyEdge edge) {
        try {
            super.addEdge(edge.getSource(), edge.getTarget(), edge);
        } catch (IllegalArgumentException e) {
            System.err.println("Can't add edge:" + edge + ".\nMake sure the vertices have been added to the graph.");

        }
    }

    public SimpleGraph getGraph(){
        return this;
    }

    @Override
    public void setEdgeWeight(Object o, double v) {

    }
}
