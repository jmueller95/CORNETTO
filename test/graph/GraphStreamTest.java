package graph;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.junit.Test;


/**
 * There are great tutorials for this API here: http://graphstream-project.org/doc/Tutorials/
 * Created by julian on 01.07.17.
 */
public class GraphStreamTest {
    Graph testGraph;

    @Test
    public void buildTestGraph() throws Exception {
        testGraph = new SingleGraph("TestGraph");
        testGraph.addNode("1");
        testGraph.addNode("2");
        testGraph.addNode("3");
        testGraph.addEdge("A", "1", "2");
        testGraph.addEdge("B", "2", "3");
        testGraph.addEdge("C", "3", "1");
        // testGraph.addEdge("D", "4", "5"); //--> ElementNotFoundException (with an actually helpful message!)

        testGraph.setStrict(false);
        testGraph.setAutoCreate(true); //NOW it works!

        testGraph.addEdge("D", "4", "5");
        testGraph.display();
        Thread.sleep(999999999);
    }

    @Test
    public void graphStructure() throws Exception {
        testGraph = new SingleGraph("TestGraph");
        testGraph.addNode("1");
        testGraph.addNode("2");
        testGraph.addEdge("A", "1", "2");
        Node n1 = testGraph.getNode("1");
        System.out.println(n1.getDegree());

        Edge edge12 = testGraph.getEdge("A");
        edge12.getNode0(); //Source node
        edge12.getNode1(); //Target node

        for (Node node : testGraph) { // alternative: testGraph.getEachNode()
            System.out.println(node.getId());
        }

        for (Edge edge: testGraph.getEachEdge()){
            System.out.println(edge.getId());
        }

        //Check if two nodes are connected:
        boolean areConnected = testGraph.getNode("1").hasEdgeBetween((Node) testGraph.getNode("2"));
        System.out.println(areConnected);

    }
}
