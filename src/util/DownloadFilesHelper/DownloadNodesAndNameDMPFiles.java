package util.DownloadFilesHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Zeth on 27.05.2017.
 */
public class DownloadNodesAndNameDMPFiles {
    private static final Logger LOGGER = Logger.getLogger(DownloadNodesAndNameDMPFiles.class.getName());
    private static final String OUTPUT_FOLDER = "./res";
    private final String NCBIURL = "ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdmp.zip";
    private final String fileName = "NodesAndNamesDmp.zip";

    /**
     * downloads the NCBI Zip und unpacks it
     * saves files to the res folder
     */
    public void DownloadNamesNodesDMPandUnzip() {
        //download the zip from NCBI
        try {
            downloadFile(new URL(NCBIURL), OUTPUT_FOLDER);
            //unzip file and save it
            unZipIt("./res" + File.separator + fileName, OUTPUT_FOLDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Unzip it
     *
     * @param url          input zip file from url
     * @param outputFolder zip file output folder
     */
    public void downloadFile(URL url, String outputFolder) {
        String fileName = "NodesAndNamesDmp.zip";
        File newFile = new File(outputFolder + File.separator + fileName);

        //download file and save it
        try {
            LOGGER.log(Level.FINE, "Downloading file...");
            FileUtils.copyURLToFile(url, newFile);
            LOGGER.log(Level.FINE, "Done downloading file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unzip it
     *
     * @param zipFile      input zip file
     * @param outputFolder zip file output folder
     */
    public void unZipIt(String zipFile, String outputFolder) throws Exception {
        byte[] buffer = new byte[1024];

        if (!zipFile.endsWith("zip")) {
            throw new IllegalArgumentException("file is not a zip file");
        }

        try {
            //create output directory if it doesn't exist yet
            File folder = new File(OUTPUT_FOLDER);
            if (!folder.exists()) {
                folder.mkdir();
            }

            //get zip file content
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            //get the ziped file list entry
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            //handle all entries
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                LOGGER.log(Level.FINE, "file unzip: " + newFile.getAbsolutePath());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fileOutputStream = new FileOutputStream(newFile);

                int length;
                while ((length = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }

                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();

            LOGGER.log(Level.FINE, "Done unpacking the zip file");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
