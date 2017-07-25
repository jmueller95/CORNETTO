package sampleParser;

import model.Sample;
import model.TaxonNode;
import model.TaxonTree;
import org.junit.Before;
import org.junit.Test;
import treeParser.TreeParser;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by julian on 07.06.17.
 */
public class BiomV1ParserTest {

    BiomV1Parser biomV1Parser;
    TaxonTree taxonTree;

    @Before
    public void setUp() throws Exception {
        TreeParser treeParser = new TreeParser();
        treeParser.parseTree();
        taxonTree = treeParser.getTaxonTree();
        biomV1Parser = new BiomV1Parser(taxonTree);
    }

    /**
     * Parses the example.biom file, tests some properties
     * @throws Exception
     */
    @Test
    public void testExampleCounts() throws Exception {
        ArrayList<Sample> samples = biomV1Parser.parse("./res/testFiles/biomFiles/example.biom");
        //There should be only one sample
        assertEquals(1, samples.size());
        //Check whether entry with id 41294 has count 0
        TaxonNode testTaxon = taxonTree.getNodeForID(41294);
        assertEquals(0,samples.get(0).getTaxa2CountMap().get(testTaxon),0);
        //Check whether entry with id 1236 has count 4
        testTaxon = taxonTree.getNodeForID(1236);
        assertEquals(4,samples.get(0).getTaxa2CountMap().get(testTaxon),0);
    }

    @Test
    public void testDenseExample() throws Exception {
        ArrayList<Sample> samples = biomV1Parser.parse("./res/testFiles/biomFiles/exV1Sparse.biom");


    }

    @Test
    public void testSparseExample() throws Exception {
        ArrayList<Sample> samples = biomV1Parser.parse("./res/testFiles/biomFiles/exV1Sparse.biom");
        assertEquals(5, samples.get(0).getTaxa2CountMap().get(taxonTree.getNodeForID(1236)), 0);

    }
}