package util.DownloadFilesHelperTest;

import model.Sample;
import org.junit.Before;

import static org.junit.Assert.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import sampleParser.TaxonId2CountCSVParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by NantiaL on 06.06.2017.
 */
public class DownloadNodesAndNameDMPFilesTest {
    @Before
    //public void setUp() throws Exception {


   // }

    @Test
    public void testDownloadile() throws Exception {
        String outputFolder = "./res";
        private final String Url = " ./res/testFiles/example.taxonId2Count.txt";
        URL zipUrl =  new URL(Url);
        assertEquals("Done downloading file",  downloadFile(zipUrl, outputFolder) );  //in order to test if there are 29 organisms

    }

}