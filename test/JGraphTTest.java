import org.jgrapht.VertexFactory;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by julian on 10.06.17.
 */
public class JGraphTTest {
    private SimpleGraph<String, DefaultEdge> stringGraph;
    private SimpleGraph<Integer, DefaultEdge> intGraph;


    @Before
    public void setUp() throws Exception {
        stringGraph = new SimpleGraph<>(DefaultEdge.class);
        intGraph = new SimpleGraph<>(DefaultEdge.class);

    }

    /**
     * Tests if a graph has zero vertices in the beginning
     *
     * @throws Exception
     */
    @Test
    public void testGraphEmpty() throws Exception {
        assertEquals(0, stringGraph.vertexSet().size());
    }

    /**
     * Adds 3 nodes to the graph, connects some, but not all, and tests these connections
     *
     * @throws Exception
     */
    @Test
    public void testSimpleGraphProperties() throws Exception {
        stringGraph.addVertex("Hello");
        stringGraph.addVertex("World");
        stringGraph.addVertex("Goodbye");
        stringGraph.addEdge("Hello", "World");
        stringGraph.addEdge("Goodbye", "World");
        //Graph is printed in the format:
        // "([<Vertex1>, <Vertex2>,...], [{<source1>,<target1>}, {<source2>,<target2>}, ...])"
        System.out.println(stringGraph);

        //They should be connected in both directions
        assertTrue(stringGraph.containsEdge("Hello", "World"));
        assertTrue(stringGraph.containsEdge("World", "Hello"));
        assertTrue(stringGraph.containsEdge("Goodbye", "World"));
        assertTrue(stringGraph.containsEdge("World", "Goodbye"));
        //Test if there's actually no connection between "Hello" and "Goodbye"
        assertFalse(stringGraph.containsEdge("Hello", "Goodbye"));
        assertFalse(stringGraph.containsEdge("Goodbye", "Hello"));
        //Vertex "World" should have 2 edges
        assertEquals(2, stringGraph.edgesOf("World").size());
    }

    /**
     * Creates a complete integer graph using a VertexFactory
     *
     * @throws Exception
     */
    @Test
    public void testCompleteGraph() throws Exception {
        CompleteGraphGenerator<Integer, DefaultEdge> intGraphGenerator = new CompleteGraphGenerator<>(10);
        VertexFactory<Integer> intVertexFactory = new VertexFactory<Integer>() {
            private int id = 0;

            @Override
            public Integer createVertex() {
                return id++;
            }
        };
        intGraphGenerator.generateGraph(intGraph, intVertexFactory, (Map) null);
        //There should be ten vertices now going from 0 to 9...
        for (int i = 0; i < 10; i++) {
            assertTrue(intGraph.containsVertex(i));
        }
        //...and no vertex for 10
        assertFalse(intGraph.containsVertex(10));

        //All of the 10 nodes should be connected
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i != j) {
                    assertTrue(intGraph.containsEdge(i, j));
                }
            }
        }
        System.out.println();
    }

    /**
     * Adds two vertices and connects them. Then removes one, adds it again and checks if the edge is gone.
     *
     * @throws Exception
     */
    @Test
    public void testRemoveVertex() throws Exception {
        int vertex1 = 42;
        int vertex2 = 43;
        intGraph.addVertex(vertex1);
        intGraph.addVertex(vertex2);
        intGraph.addEdge(vertex1, vertex2);
        assertEquals(1, intGraph.degreeOf(vertex1));
        intGraph.removeVertex(vertex1);
        intGraph.addVertex(vertex1);
        assertEquals(0, intGraph.degreeOf(vertex1));
    }

    @Test
    public void miscTesting() throws Exception {

    }
}
