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

    @Test
    public void testgetUnifiedTaxonList() throws Exception {
        TreeParser parser = new TreeParser();
        parser.parseTree("./res/nodes.dmp", "./res/names.dmp");
        TaxonId2CountCSVParser csvParser = new TaxonId2CountCSVParser(parser.getTaxonTree());
        ArrayList<Sample> samples = csvParser.parse("./res/testFiles/megan_examples/core1_activelayer_day2-ID2Count.txt");

        LinkedList<TaxonNode> unifiedTaxonList = SampleComparison.getUnifiedTaxonList(samples, "phylum");
        //There is only one "phylum" in the sample (Proteobacteria), so the list should have the size 1 now
        assertEquals(1, unifiedTaxonList.size());

        unifiedTaxonList = SampleComparison.getUnifiedTaxonList(samples, "notAValidRank");
        //Now it should have the size 0
        assertEquals(0, unifiedTaxonList.size());

        unifiedTaxonList = SampleComparison.getUnifiedTaxonList(samples, "class");
        //There are seven classes in this phylum:
        // Gammaproteobacteria, Alphaproteobacteria, Betaproteobacteria, Deltaproteobacteria,
//                Epsilonproteobacteria, Zetaproteobacteria, Acidithiobacillia
        assertEquals(7, unifiedTaxonList.size());

    }

    @Test
    public void testCorrelation() throws Exception {
        TreeParser parser = new TreeParser();
        parser.parseTree("./res/nodes.dmp", "./res/names.dmp");
        TaxonId2CountCSVParser csvParser = new TaxonId2CountCSVParser(parser.getTaxonTree());
        ArrayList<Sample> samples = new ArrayList<>();
        samples.addAll(csvParser.parse("./res/testFiles/megan_examples/core1_activelayer_day2-ID2Count.txt"));
        samples.addAll(csvParser.parse("./res/testFiles/megan_examples/core1_activelayer_day7-ID2Count.txt"));
        samples.addAll(csvParser.parse("./res/testFiles/megan_examples/core1_activelayer_frozen-ID2Count.txt"));


        RealMatrix correlationMatrix = SampleComparison.getCorrelationMatrixOfSamples(samples, "class");
        System.out.println("Correlation Matrix:");
        printMatrix(correlationMatrix);
        System.out.println();

        RealMatrix correlationPValues = SampleComparison.getCorrelationPValuesOfSamples(samples, "class");
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