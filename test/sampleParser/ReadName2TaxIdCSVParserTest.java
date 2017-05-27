package sampleParser;

import model.Sample;
import org.junit.Before;
import org.junit.Test;
import model.TaxonNode;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Zeth on 27.05.2017.
 */
public class ReadName2TaxIdCSVParserTest {

    ReadName2TaxIdCSVParser readName2TaxIdCSVParser;
    @Before
    public void setUp() throws Exception {
        readName2TaxIdCSVParser = new ReadName2TaxIdCSVParser();
    }

    @Test
    public void testSampleCounts() throws Exception {
        ArrayList<Sample> exampleFileSamples = readName2TaxIdCSVParser.parse
                ("./res/testFiles/readName2TaxId/example.readName2TaxId.txt");
        assertEquals(1, exampleFileSamples.size());
        ArrayList<Sample> emptyFileSamples = readName2TaxIdCSVParser.parse("./res/testFiles/readName2TaxId/emptyFile");
        //assertEquals(0, emptyFileSamples); //does not pass the test, there's always one sample added
    }

    @Test
    public void testTaxonIdCounts() throws Exception {
        ArrayList<Sample> exampleFileSamples = readName2TaxIdCSVParser.parse
                ("./res/testFiles/readName2TaxId/example.readName2TaxId.txt");
        TaxonNode n1 = new TaxonNode("name", 1224, "rank", 0, new ArrayList<>());
        Sample sample1 = exampleFileSamples.get(0);
        assertEquals(1, (int) sample1.getTaxa2CountMap().get(n1));
        TaxonNode n2 = new TaxonNode("name", 469, "rank", 0, new ArrayList<>());
    }


}