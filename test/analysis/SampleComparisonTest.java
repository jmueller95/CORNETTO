package analysis;

import model.Sample;
import model.TaxonNode;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Before;
import org.junit.Test;
import sampleParser.TaxonId2CountCSVParser;
import treeParser.TreeParser;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by julian on 10.06.17.
 */
public class SampleComparisonTest {
    private TaxonNode node1, node2, node3, node4;

    @Before
    public void setUp() throws Exception {
        node1 = new TaxonNode(1, null, 0);
        node2 = new TaxonNode(2, null, 0);
        node3 = new TaxonNode(3, null, 0);
        node4 = new TaxonNode(4, null, 0);
    }

    /**
     * Create two samples, the first contains nodes 1,2 and 3, the second 2,3 and 4.
     * Tests if the unifiedTaxa2CountMap has length 4 and if the counts are put correctly
     *
     * @throws Exception
     */
    @Test
    public void testGetUnifiedTaxa2CountMap() throws Exception {
        Sample sample1 = new Sample();
        sample1.getTaxa2CountMap().put(node1, 10);
        sample1.getTaxa2CountMap().put(node2, 20);
        sample1.getTaxa2CountMap().put(node3, 0);
        Sample sample2 = new Sample();
        sample2.getTaxa2CountMap().put(node2, 200);
        sample2.getTaxa2CountMap().put(node3, 300);
        sample2.getTaxa2CountMap().put(node4, 0);

        HashMap<TaxonNode, int[]> unifiedTaxa2CountMap = SampleComparison.getUnifiedTaxa2CountMap(sample1, sample2);
        assertEquals(4, unifiedTaxa2CountMap.size());
        assertEquals(10, unifiedTaxa2CountMap.get(node1)[0]);
        assertEquals(0, unifiedTaxa2CountMap.get(node1)[1]);
        assertEquals(20, unifiedTaxa2CountMap.get(node2)[0]);
        assertEquals(200, unifiedTaxa2CountMap.get(node2)[1]);
        assertEquals(0, unifiedTaxa2CountMap.get(node3)[0]);
        assertEquals(300, unifiedTaxa2CountMap.get(node3)[1]);
        assertEquals(0, unifiedTaxa2CountMap.get(node4)[0]);
        assertEquals(0, unifiedTaxa2CountMap.get(node4)[1]);
    }


    @Test
    public void testCorrelation() throws Exception {
        Sample sample1 = new Sample();
        sample1.getTaxa2CountMap().put(node1, 10);
        sample1.getTaxa2CountMap().put(node2, 20);
        sample1.getTaxa2CountMap().put(node3, 30);
        sample1.getTaxa2CountMap().put(node4, 40);

        Sample sample2 = new Sample();
        sample2.getTaxa2CountMap().put(node1, 20);
        sample2.getTaxa2CountMap().put(node2, 40);
        sample2.getTaxa2CountMap().put(node3, 15);
        sample2.getTaxa2CountMap().put(node4, 20);

        RealMatrix correlationMatrix = SampleComparison.getCorrelationMatrixForSamples(sample1, sample2);
        printMatrix(correlationMatrix);

    }

    @Test
    public void testRealCorrelation() throws Exception {
        TreeParser parser = new TreeParser();
        parser.parseTree("./res/nodes.dmp", "./res/names.dmp");
        TaxonId2CountCSVParser csvParser = new TaxonId2CountCSVParser(parser.getTaxonTree());
        ArrayList<Sample> samples = csvParser.parse("./res/testFiles/taxonId2Count/multipleSampleExample.taxonId2Count.txt");
        RealMatrix correlationMatrix = SampleComparison.getCorrelationMatrixForSamples(samples.get(0), samples.get(1));
        printMatrix(correlationMatrix);
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
                System.out.printf("%.3f", matrix.getEntry(rowIndex,colIndex));
                System.out.print("\t");
            }
            System.out.println();
        }
    }
}