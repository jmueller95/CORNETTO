package graph;

import org.jgrapht.graph.SimpleGraph;

/**
 * Created by julian on 10.06.17.
 */
public class MyGraph extends SimpleGraph { //TODO: Is SimpleGraph enough or should we use another graph class?

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
}
