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
        download.unZipIt("TwoZipFilesTest",  "./res/zipFileTest", fileList);
    }



}