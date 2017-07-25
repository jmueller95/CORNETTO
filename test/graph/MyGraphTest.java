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
        MyVertex v1 = new MyVertex(new TaxonNode(1,"rank", 1),3);
        MyVertex v2 = new MyVertex(new TaxonNode(2, "rank", 1),3);
        MyVertex v3 = new MyVertex(new TaxonNode(3, "rank", 1),3);
        MyVertex v4 = new MyVertex(new TaxonNode(4, "rank", 1),3);
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
     //TODO: Implement

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