package sampleParser;

import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;
import model.Sample;
import model.TaxonTree;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import treeParser.TreeParser;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by caspar on 24.05.17.
 */
public class BiomV2ParserTest {

    BiomV2Parser biomV2Parser;
    TaxonTree taxonTree;

    
    @Before
    public void setUp() throws Exception {
        TreeParser treeParser = new TreeParser();
        treeParser.parseTree();
        taxonTree = treeParser.getTaxonTree();
        biomV2Parser = new BiomV2Parser(taxonTree);

    }

    @Test
    public void readBiomV2File() throws Exception {
        File biomV2File = new File ("./res/testFiles/biomFiles/exampleV2.biom");
        // Check if biomFile does exist.
        Assert.assertTrue(biomV2File.exists());

    }

    @Test
    public void checkSampleNumber() throws Exception {
        // Check if correct Number of samples has been found
        ArrayList<Sample> sampleList = biomV2Parser.parse("./res/testFiles/biomFiles/exampleV2.biom");
        Assert.assertEquals(1, sampleList.size());
    }

    @Test
    public void checkSampleValue() throws Exception {
        // Check if value of sample is correct
        ArrayList<Sample> sampleList = biomV2Parser.parse("./res/testFiles/biomFiles/exampleV2.biom");
        Assert.assertEquals(1, (int) sampleList.get(0).getTaxa2CountMap().get(taxonTree.getNodeForID(1224)));
        Assert.assertEquals(2, (int) sampleList.get(0).getTaxa2CountMap().get(taxonTree.getNodeForID(469)));
    }

    @Test
    public void checkMetaData() throws Exception {
        ArrayList<Sample> sampleList = biomV2Parser.parse("./res/testFiles/biomFiles/exampleV2.biom");
    }

}
