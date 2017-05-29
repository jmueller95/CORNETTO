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
            csvparser = new TaxonId2CountCSVParser(taxonTree);

        }

    /**
     * Checks if the test file contains 29 samples,as expected
     * @throws Exception
     */

        @Test
        public void testSampleCounts() throws Exception {
            ArrayList<Sample> samples = csvparser.parse("./res/testFiles/example.taxonId2Count.txt");
            assertEquals(29,samples.size());
        }

    /**
     * Checks if there is only one entry in the tst file with the id 286
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
        TaxonNode n1 = new TaxonNode("name", 1, "rank", 0, new ArrayList<>());
        TaxonNode n2 = new TaxonNode("name", 356, "rank", 0, new ArrayList<>());
        assertEquals(1,(int) sample.getTaxa2CountMap().get(n1));//expected result with id 356 :1

    }



    }

