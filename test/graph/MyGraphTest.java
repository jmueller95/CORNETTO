package graph;

import analysis.SampleComparison;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import model.Sample;
import model.TaxonNode;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by julian on 10.06.17.
 */
public class MyGraphTest {

    MyGraph<MyVertex, MyEdge> g;

    @Before
    public void setUp() throws Exception {
        g = new MyGraph<>();
    }

    /**
     * Creates example MyGraph, tests some properties
     *
     * @throws Exception
     */
    @Test
    public void testGraphProperties() throws Exception {
        MyVertex v1 = new MyVertex(new TaxonNode(1,"rank", 1));
        MyVertex v2 = new MyVertex(new TaxonNode(2, "rank", 1));
        MyVertex v3 = new MyVertex(new TaxonNode(3, "rank", 1));
        MyVertex v4 = new MyVertex(new TaxonNode(4, "rank", 1));
        MyEdge edge12 = new MyEdge(v1, v2);
        MyEdge edge23 = new MyEdge(v2, v3);
        MyEdge edge34 = new MyEdge(v3, v4);
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addEdge(edge12, v1, v2);
        g.addEdge(edge23, v2, v3);
        g.addEdge(edge34, v3, v4);

        //There should be three vertices and two edges now
        assertEquals(3, g.getVertices().size());
        assertEquals(2, g.getEdges().size());

        //v1 and v2 should be connected, but NOT v1 and v3
        assertTrue(g.containsEdge(edge12));
        assertFalse(g.containsEdge(new MyEdge(v1, v3)));
    }

    @Test
    public void testFilterTaxa() throws Exception {
        TaxonNode node1 = new TaxonNode(1, null, 0);
        TaxonNode node2 = new TaxonNode(2, null, 0);
        TaxonNode node3 = new TaxonNode(3, null, 0);
        TaxonNode node4 = new TaxonNode(4, null, 0);

        Sample sample1 = new Sample();
        sample1.getTaxa2CountMap().put(node1, 10);
        sample1.getTaxa2CountMap().put(node2, 20);
        sample1.getTaxa2CountMap().put(node3, 30);
        sample1.getTaxa2CountMap().put(node4, 40);

        Sample sample2 = new Sample();
        sample2.getTaxa2CountMap().put(node1, 20);
        sample2.getTaxa2CountMap().put(node2, 40);
        sample2.getTaxa2CountMap().put(node3, 15);
        sample2.getTaxa2CountMap().put(node4, 100);

        Sample sample3 = new Sample();
        sample3.getTaxa2CountMap().put(node1, 5);
        sample3.getTaxa2CountMap().put(node2, 10);
        sample3.getTaxa2CountMap().put(node3, 60);
        sample3.getTaxa2CountMap().put(node4, 100);

        ArrayList<Sample> samples = new ArrayList<>();
        samples.add(sample1);
        samples.add(sample2);
        samples.add(sample3);

        MyVertex vertex1 = new MyVertex(node1);
        MyVertex vertex2 = new MyVertex(node2);
        MyVertex vertex3 = new MyVertex(node3);
        MyVertex vertex4 = new MyVertex(node4);

        //TODO: This needs to be done automatically when initializing the graph!
        g.getTaxonNodeToVertexMap().put(node1,vertex1);
        g.getTaxonNodeToVertexMap().put(node2,vertex2);
        g.getTaxonNodeToVertexMap().put(node3,vertex3);
        g.getTaxonNodeToVertexMap().put(node4,vertex4);
        HashMap<Integer, MyEdge> edgesOfNode1 = new HashMap<>();
        HashMap<Integer, MyEdge> edgesOfNode2 = new HashMap<>();
        HashMap<Integer, MyEdge> edgesOfNode3 = new HashMap<>();
        HashMap<Integer, MyEdge> edgesOfNode4 = new HashMap<>();
        edgesOfNode1.put(2, new MyEdge(vertex1, vertex2));
        //...



        RealMatrix correlationMatrix = SampleComparison.getCorrelationMatrixOfSamples(samples);
        System.out.println("Correlation Matrix:");
        printMatrix(correlationMatrix);
        System.out.println();

        RealMatrix correlationPValues = SampleComparison.getCorrelationPValuesOfSamples(samples);
        System.out.println("P-Value matrix:");
        printMatrix(correlationPValues);


        g.filterSamplesByPValue(samples, 0);
        /**
         * This is the p-Value matrix of the dataset:
         0,000	0,000	0,242	0,879
         0,000	0,000	0,242	0,879
         0,242	0,242	0,000	0,879
         0,879	0,879	0,879	0,000
         * So vertices 3 and 4 should be hidden now, and vertices 1 and 2 should only have one edge remaining
         */
        assertTrue(vertex3.isHidden()&&vertex4.isHidden());

                /*MyVertex v1 = new MyVertex("Content"); //Has string as content
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
        assertFalse(graph.containsEdge(v1, v3)); */

    }


    @Test
    public void testHideVertex() throws Exception {

    }

    @Test
    public void testHideEdge() throws Exception {

    }

    /**
     * Helper method for printing a matrix
     *
     * @param matrix
     */
    public void printMatrix(RealMatrix matrix) {
        for (int rowIndex = 0; rowIndex < matrix.getRowDimension(); rowIndex++) {
            double[] currentRow = matrix.getRow(rowIndex);
            for (int colIndex = 0; colIndex < matrix.getColumnDimension(); colIndex++) {
                System.out.printf("%.3f", matrix.getEntry(rowIndex, colIndex));
                System.out.print("\t");
            }
            System.out.println();
        }
    }
}