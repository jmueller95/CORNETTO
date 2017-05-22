package sampleParser;

import model.Sample;
import model.TaxonNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by jm on 22.05.17.
 */
public class TaxonId2CountCSVParserTest {
    TaxonId2CountCSVParser csvparser;
    @Before
    public void setUp() throws Exception {
        csvparser = new TaxonId2CountCSVParser();

    }

//    @Test
//    public void testSampleCounts() throws Exception {
//        ArrayList<Sample> samples = csvparser.parse("...");
//        assertEquals(3,samples.size());
//    }
//
//    @Test
//    public void testCountsOfFirstSample() throws Exception{
//        ArrayList<Sample> samples = csvparser.parse("...");
//        Sample sample = samples.get(0);
//        TaxonNode n1 = new TaxonNode("name", 1, "rank", 0, new ArrayList<>());
//        TaxonNode n2 = new TaxonNode("name", 2, "rank", 0, new ArrayList<>());
//        assertEquals(10,(int) sample.getTaxa2CountMap().get(0));
//    }

}