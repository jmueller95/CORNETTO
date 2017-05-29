package sampleParser;

import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;
import model.Sample;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by caspar on 24.05.17.
 */
public class BiomV2ParserTest {

    BiomV2Parser biomV2Parser;

    
    @Before
    public void setUp() throws Exception {
        biomV2Parser = new BiomV2Parser();

    }

    @Test
    public void readBiomV2File() throws Exception {
        File biomV2File = new File ("res/testFiles/biomFiles/exV2.biom");
        // Check if biomFile does exist.
        Assert.assertTrue(biomV2File.exists());
    }

    @Test
    public void checkSampleNumber() throws Exception {
        // Check if correct Number of samples has been found
        ArrayList<Sample> sampleList = biomV2Parser.parse("res/testFiles/biomFiles/exV2.biom");
        Assert.assertEquals(6, sampleList.size());
    }

}
