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
    private TaxonNode node1, node2, node3, node4, node5, node6, node7, node8, node9, node10;

    @Before
    public void setUp() throws Exception {
        node1 = new TaxonNode(1, null, 0);
        node2 = new TaxonNode(2, null, 0);
        node3 = new TaxonNode(3, null, 0);
        node4 = new TaxonNode(4, null, 0);
        node5 = new TaxonNode(5, null, 0);
        node6 = new TaxonNode(6, null, 0);
        node7 = new TaxonNode(7, null, 0);
        node8 = new TaxonNode(8, null, 0);
        node9 = new TaxonNode(9, null, 0);
        node10 = new TaxonNode(10, null, 0);
    }

    @Test
    public void testgetUnifiedTaxonList() throws Exception {
        Sample sample1 = new Sample();
        sample1.getTaxa2CountMap().put(node1, 0);
        sample1.getTaxa2CountMap().put(node2, 0);
        sample1.getTaxa2CountMap().put(node3, 0);
        Sample sample2 = new Sample();
        sample2.getTaxa2CountMap().put(node2, 0);
        sample2.getTaxa2CountMap().put(node3, 0);
        sample2.getTaxa2CountMap().put(node4, 0);
        Sample sample3 = new Sample();
        sample3.getTaxa2CountMap().put(node5,0);
        sample3.getTaxa2CountMap().put(node10,0);
        Sample sample4 = new Sample();
        sample4.getTaxa2CountMap().put(node9,0);
        sample4.getTaxa2CountMap().put(node8,0);
        sample4.getTaxa2CountMap().put(node7,0);
        sample4.getTaxa2CountMap().put(node6,0);

        ArrayList<Sample> sampleList = new ArrayList<>();
        sampleList.add(sample1);
        sampleList.add(sample2);
        sampleList.add(sample3);
        sampleList.add(sample4);

        LinkedList<TaxonNode> unifiedTaxonList = SampleComparison.getUnifiedTaxonList(sampleList);
        //This list should have length 10 and be sorted by Taxon id
        assertEquals(10,unifiedTaxonList.size());
        assertEquals(1,unifiedTaxonList.get(0).getTaxonId());
        assertEquals(10,unifiedTaxonList.get(9).getTaxonId());
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
        sample2.getTaxa2CountMap().put(node4, 100);

        Sample sample3 = new Sample();
        sample3.getTaxa2CountMap().put(node1,5);
        sample3.getTaxa2CountMap().put(node2,10);
        sample3.getTaxa2CountMap().put(node3,60);
        sample3.getTaxa2CountMap().put(node4,100);

        ArrayList<Sample> samples = new ArrayList<>();
        samples.add(sample1);
        samples.add(sample2);
        samples.add(sample3);

        RealMatrix correlationMatrix = SampleComparison.getCorrelationMatrixOfSamples(samples);
        System.out.println("Correlation Matrix:");
        printMatrix(correlationMatrix);
        System.out.println();

        RealMatrix correlationPValues = SampleComparison.getCorrelationPValuesOfSamples(samples);
        System.out.println("P-Value matrix:");
        printMatrix(correlationPValues);


    }

    @Test
    public void testRealExampleCorrelation() throws Exception {
        TreeParser parser = new TreeParser();
        parser.parseTree("./res/nodes.dmp", "./res/names.dmp");
        TaxonId2CountCSVParser csvParser = new TaxonId2CountCSVParser(parser.getTaxonTree());
        ArrayList<Sample> samples = csvParser.parse("./res/testFiles/taxonId2Count/multipleSampleExample.taxonId2Count.txt");

        RealMatrix correlationMatrix = SampleComparison.getCorrelationMatrixOfSamples(samples);
        System.out.println("Correlation Matrix:");
        printMatrix(correlationMatrix);
        System.out.println();

        RealMatrix correlationPValues = SampleComparison.getCorrelationPValuesOfSamples(samples);
        System.out.println("P-Value matrix:");
        printMatrix(correlationPValues);
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

    /**
     * Helper method for printing a "HashMatrix", i.e. a HashMap that contains HashMaps for every node
     *
     * @param matrix
     */
    public void printHashMatrix(HashMap<TaxonNode, HashMap<TaxonNode, Double>> matrix) {
        //Sort the keys
        ArrayList<TaxonNode> taxonNodeList = new ArrayList<>(matrix.keySet());
        taxonNodeList.sort((tn1, tn2) -> {
            int id1 = tn1.getTaxonId();
            int id2 = tn2.getTaxonId();
            return (id1 > id2 ? 1 : (id1 == id2 ? 0 : -1));
        });

        System.out.print("\t");
        //Print ids as header
        for (TaxonNode taxonNode : taxonNodeList) {
            System.out.printf("%d", taxonNode.getTaxonId());
            System.out.print("\t\t");
        }
        System.out.println();
        //Print every inner hashmap in order
        for (TaxonNode firstNode : taxonNodeList) {
            System.out.print(firstNode.getTaxonId() + "\t");
            for (TaxonNode secondNode : taxonNodeList) {
                System.out.printf("%.3f", matrix.get(firstNode).get(secondNode));
                System.out.print("\t");
            }
            System.out.println();
        }
    }
}