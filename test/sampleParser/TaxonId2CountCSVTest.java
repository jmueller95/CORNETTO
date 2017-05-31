package sampleParser;

import model.Sample;
import model.TaxonNode;
import model.TaxonTree;
import org.junit.Before;
import org.junit.Test;
import treeParser.TreeParser;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by NantiaL on 27.05.2017.
 */
public class TaxonId2CountCSVTest {

    TaxonId2CountCSVParser csvparser;
    TaxonTree taxonTree;

    /**
     * Creates the taxonTree, sets up the parser
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        TreeParser treeParser = new TreeParser();
        treeParser.parseTree();
        taxonTree = treeParser.getTaxonTree();
        csvparser = new TaxonId2CountCSVParser(taxonTree);

    }

    /**
     * Parse a file with three samples, checks if length of sample list is three and tests some properties
     * @throws Exception
     */
    @Test
    public void testMultipleSamples() throws Exception {
        ArrayList<Sample> samples = csvparser.parse("./res/testFiles/taxonId2Count/multipleSampleExample.taxonId2Count.txt");
        assertEquals(3,samples.size());
        TaxonNode testTaxon = taxonTree.getNodeForID(80864);
        assertEquals(1,samples.get(0).getTaxa2CountMap().get(testTaxon),0);
        assertEquals(2,samples.get(1).getTaxa2CountMap().get(testTaxon),0);
        assertEquals(13,samples.get(2).getTaxa2CountMap().get(testTaxon),0);

    }

    /**
     * Tests if the count of the taxon with id 543 actually matches 12
     * and:                                    267888               0
     *
     * @throws Exception
     */
    @Test
    public void testCounts() throws Exception {
        ArrayList<Sample> samples = csvparser.parse("./res/testFiles/taxonId2Count/example.taxonId2Count.txt");
        TaxonNode testTaxon = taxonTree.getNodeForID(543);
        assertEquals(12, samples.get(0).getTaxa2CountMap().get(testTaxon), 0);
        testTaxon = taxonTree.getNodeForID(267888);
        assertEquals(0, samples.get(0).getTaxa2CountMap().get(testTaxon), 0);
    }
}

