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

    ReadName2TaxIdCSVParser readName2TaxIdCSVParser;
    TaxonTree taxonTree;
    @Before
    public void setUp() throws Exception {
        TreeParser treeParser = new TreeParser();
        treeParser.setFileNamesDmp("./res/testFiles/treeParser/names_stub.dmp");
        treeParser.setFileNodesDmp("./res/testFiles/treeParser/nodes_stub.dmp");
        treeParser.readNodesDmpFile();
        treeParser.readNamesDmpFile();

        TaxonNode rootNode = new TaxonNode("root", 1, "no rank", 1, new ArrayList<>());
        taxonTree = new TaxonTree(rootNode);
        taxonTree.buildTree(treeParser.getNodesDmps(), treeParser.getNamesDmps());

        readName2TaxIdCSVParser = new ReadName2TaxIdCSVParser(taxonTree);
    }

    @Test
    public void testSampleCounts() throws Exception {
        ArrayList<Sample> exampleFileSamples = readName2TaxIdCSVParser.parse
                ("./res/testFiles/readName2TaxId/example.readName2TaxId.txt");
        assertEquals(1, exampleFileSamples.size());
        ArrayList<Sample> emptyFileSamples = readName2TaxIdCSVParser.parse("./res/testFiles/readName2TaxId/emptyFile");
        assertEquals(0, emptyFileSamples); //does not pass the test, there's always one sample added
    }


    //TODO fix these tests
    @Test
    public void testTaxonIdCounts() throws Exception {
        ArrayList<Sample> exampleFileSamples = readName2TaxIdCSVParser.parse
                ("./res/testFiles/readName2TaxId/example.readName2TaxId.txt");
        Sample sample1 = exampleFileSamples.get(0);

        final HashMap<Integer, TaxonNode> treeStructure = taxonTree.getTreeStructure();
        TaxonNode testNode = treeStructure.get(7);

        assertEquals(1, (int) sample1.getTaxa2CountMap().get(testNode)); //does not pass the test because of a null
        // pointer exception
        TaxonNode n2 = new TaxonNode("name", 469, "rank", 0, new ArrayList<>());
        assertEquals(2, (int) sample1.getTaxa2CountMap().get(n2)); //does not pass the test because of a null pointer
        // exception
    }


}