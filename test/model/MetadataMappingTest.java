package model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by NantiaL on 11.07.2017.
 */
public class MetadataMappingTest {


    MetadataMapping csvparser;
    @Before
    public void setUp() throws Exception {

        csvparser = new MetadataMapping();

    }


    @Test
     public void metadataParser() throws Exception {

        ArrayList<String> show =  csvparser.MetadataParser();


        assertEquals( 0, show.size() ); //checks if the size of the file is correct



    }

}
