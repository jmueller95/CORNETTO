package model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

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
        //HashMap<String,String> show =  csvparser.MetadataParser("./res/testFiles/metadataFilesTest");
        ArrayList<String> show =  csvparser.MetadataParser("./res/testFiles/metadataFilesTest");


        assertEquals( 0, show.size() ); //checks if the size of the file is correct



    }

}
