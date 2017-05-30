package sampleParser;

import model.Sample;
import model.TaxonNode;
import model.TaxonTree;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by NantiaL on 27.05.2017.
 */

public class TaxonId2CountCSVParserTest {

    TaxonId2CountCSVParser csvparser;
    TaxonTree taxonTree; //TODO: Parse the tree and get it here!

        @Before
        public void setUp() throws Exception {
          //  TaxonId2CountCSVParser csvparser;
           // TaxonTree taxonTree; //TODO: Parse the tree and get it here!

            csvparser = new TaxonId2CountCSVParser(taxonTree);

        }

    /**
     * Checks if the test file contains 29 organisms and 1 sample as expected
     * @throws Exception
     */

        @Test
        public void testSampleCounts() throws Exception {
            ArrayList<Sample> samples = csvparser.parse("./res/testFiles/example.taxonId2Count.txt");
            assertEquals(1,samples.size()); //tests the number of samples
            assertEquals(29,  samples.get(0).getTaxa2CountMap() );  //in order to test if there are 29 organisms

        }

    /**
     * Checks if there is only one entry in the test file with the id 286
     * @throws Exception
     */

    @Test
    public void testCountsOfFirstSample() throws Exception{
            ArrayList<Sample> samples = csvparser.parse("./res/testFiles/example.taxonId2Count.txt");
            Sample sample = samples.get(0);
            TaxonNode n3 = new TaxonNode("name", 286, "rank", 0, new ArrayList<>());
            assertEquals(1, (int) sample.getTaxa2CountMap().get(n3)); //expected result with id 286 :1
        }

    @Test
    public void testCountsOfFirstSample2() throws Exception{
        ArrayList<Sample> samples = csvparser.parse("./res/testFiles/example.taxonId2Count.txt");
        Sample sample = samples.get(0);
        TaxonNode node = taxonTree.getNodeForID(1224);
        TaxonNode node1 = taxonTree.getNodeForID(135622);
        assertEquals(1,(int) sample.getTaxa2CountMap().get(node)); //tests if the id 1224 has the count 1 with an integer
        assertEquals(0,(int) sample.getTaxa2CountMap().get(node1)); //with space after comma

    }

    @Test
    public void testMultipleSamples() throws Exception{

        ArrayList<Sample> samples = csvparser.parse("./res/testFiles/example.taxonID2CountMultipleSamples.txt");
        Sample sample = samples.get(0);
        TaxonNode node2 = taxonTree.getNodeForID(1221);
        assertEquals(1,(int) sample.getTaxa2CountMap().get(node2)); //tests if the id 1221 has the count 1 (multipleSamples)
    }



    }

