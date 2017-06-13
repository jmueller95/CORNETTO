package util.DownloadFilesHelper;

import org.apache.commons.io.FileUtils;

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
 * Created by Zeth on 27.05.2017.
 */
public class DownloadNodesAndNameDMPFiles {
    private static final Logger LOGGER = Logger.getLogger(DownloadNodesAndNameDMPFiles.class.getName());
    private static final String OUTPUT_FOLDER = "./res";
    private final String NCBIURL = "ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdmp.zip";
    private final String zipFileToDownload = "NodesAndNamesDmp.zip";
    private final ArrayList<String> listOfFilesToUnzip = new ArrayList() {{
        add("names.dmp");
        add("nodes.dmp");
    }};

    /**
     * downloads the NCBI Zip und unpacks it
     * saves files to the res folder
     * deletes the zip file afterwards
     */
    public void DownloadNamesNodesDMPandUnzip() {
        //download the zip from NCBI
        try {
            downloadFile(new URL(NCBIURL), OUTPUT_FOLDER);
            //unzip file and save it
            unZipIt("./res" + File.separator + zipFileToDownload, OUTPUT_FOLDER, listOfFilesToUnzip);
            File file = new File("./res" + File.separator + zipFileToDownload);
            file.delete();
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
     * @param listOfFilesToUnzip list of files that are to be extracted from the zip
     */
    public void unZipIt(String zipFile, String outputFolder, ArrayList<String> listOfFilesToUnzip) throws Exception {
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
            ZipEntry zipEntry;

            //handle all entries
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                //check if the entry is one of the files that need to be unzipped
                for (String item : listOfFilesToUnzip) {
                    //we found a file to unzip
                    if (item.equals(fileName)) {
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
                    }
                }
            }
            zipInputStream.closeEntry();
            zipInputStream.close();

            LOGGER.log(Level.FINE, "Done unpacking the zip file");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
