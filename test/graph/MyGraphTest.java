package graph;

import model.TaxonNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by julian on 10.06.17.
 */
public class MyGraphTest {
    private MyGraph graph;

    @Before
    public void setUp() throws Exception {
        graph = new MyGraph();
    }

    /**
     * Creates example MyGraph, tests some properties
     *
     * @throws Exception
     */
    @Test
    public void testGraphProperties() throws Exception {
        MyVertex v1 = new MyVertex("Content"); //Has string as content
        MyVertex v2 = new MyVertex(42.0); //Has double as content
        MyVertex v3 = new MyVertex(new TaxonNode(1234, "rank", 1)); //Has TaxonNode as content
        MyVertex v4 = new MyVertex("stuff");
        MyEdge edge12 = new MyEdge(v1, v2);
        MyEdge edge23 = new MyEdge(v2, v3);
        MyEdge edge34 = new MyEdge(v3, v4);
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addEdge(edge12, 10);
        graph.addEdge(edge23, 20);
        graph.addEdge(edge34, 20);

        //There should be three vertices and two edges now
        assertEquals(3, graph.vertexSet().size());
        assertEquals(2, graph.edgeSet().size());

        //v1 and v2 should be connected, but NOT v1 and v3
        assertTrue(graph.containsEdge(v1, v2));
        assertFalse(graph.containsEdge(v1, v3));
    }

    @Test
    public void testHideVertex() throws Exception {

    }

    @Test
    public void testHideEdge() throws Exception {

    }
}