package sampleParser;

import model.Sample;
import model.TaxonTree;
import org.junit.Before;
import org.junit.Test;
import model.TaxonNode;
import treeParser.TreeParser;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Zeth on 27.05.2017.
 */
public class ReadName2TaxIdCSVParserTest {

    ReadName2TaxIdCSVParser csvparser;
    TaxonTree taxonTree;

    @Before
    public void setUp() throws Exception {
        TreeParser treeParser = new TreeParser();
        treeParser.parseTree();
        taxonTree = treeParser.getTaxonTree();
        csvparser = new ReadName2TaxIdCSVParser(taxonTree);

    }

    /**
     * Parses readName2TaxId file, tests if taxon with id 590 matches count 3 and id 561 matches 48
     * @throws Exception
     */
    @Test
    public void testCounts() throws Exception {
        ArrayList<Sample> samples = csvparser.parse("./res/testFiles/readName2TaxId/example.readName2TaxId.txt");
        TaxonNode testNode = taxonTree.getNodeForID(590);
        assertEquals(3,samples.get(0).getTaxa2CountMap().get(testNode),0);
        testNode = taxonTree.getNodeForID(561);
        assertEquals(48,samples.get(0).getTaxa2CountMap().get(testNode),0);

    }


}