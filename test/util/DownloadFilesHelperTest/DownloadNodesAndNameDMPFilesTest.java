package util.DownloadFilesHelperTest;

import org.junit.Before;
import org.junit.Test;
import util.DownloadFilesHelper.DownloadNodesAndNameDMPFiles;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


/**
 * Created by NantiaL on 06.06.2017.
 */
public class DownloadNodesAndNameDMPFilesTest {
    DownloadNodesAndNameDMPFiles download;

    @Before
    public void setUp() throws Exception {
        download = new DownloadNodesAndNameDMPFiles();

    }


    @Test
    public void testDownloadAndUnpackFile() throws Exception {
        download.DownloadNamesNodesDMPandUnzip();
    }


    @Test
    public void testUnzipIt() throws Exception {
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("example.readName2TaxId.txt");
        fileList.add("example.taxonId2Count.txt");
        ArrayList<String> emptyList = new ArrayList<>();

        download.unZipIt("./res/testFiles/zipFileTest/TwoZipFilesTest.zip",  "./res/zipFileTest", fileList);
        assertEquals(2, fileList.size()); //tests if the zip File contains 2 files
        assertEquals(0, emptyList.size()); //tests if the zip File is empty
        assertEquals("example.readName2TaxId.txt", fileList.get(0)); //tests if example.readName2TaxId.txt file is the first one
        assertEquals("example.taxonId2Count.txt", fileList.get(1));
    }




}