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
        //read required files for the tree
        TreeParser treeParser = new TreeParser();
        treeParser.setFileNamesDmp("./res/testFiles/treeParser/names_stub.dmp");
        treeParser.setFileNodesDmp("./res/testFiles/treeParser/nodes_stub.dmp");
        treeParser.readNodesDmpFile();
        treeParser.readNamesDmpFile();

        //create root node and build tree
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
        assertEquals(0, emptyFileSamples.size());
    }

    @Test
    public void testTaxonIdCounts() throws Exception {
        ArrayList<Sample> exampleFileSamples = readName2TaxIdCSVParser.parse
                ("./res/testFiles/readName2TaxId/example.readName2TaxId_stub.txt");
        Sample sample1 = exampleFileSamples.get(0);

        final HashMap<Integer, TaxonNode> treeStructure = taxonTree.getTreeStructure();
        TaxonNode AzorhizobiumCaulinodansNode = treeStructure.get(7);

        assertEquals(3, (int) sample1.getTaxa2CountMap().get(AzorhizobiumCaulinodansNode));
    }


}