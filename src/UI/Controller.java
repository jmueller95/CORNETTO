package UI;

import javafx.fxml.Initializable;
import model.TaxonTree;
import treeParser.TreeParser;
import util.DownloadFilesHelper.DownloadNodesAndNameDMPFiles;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Zeth on 15.06.2017.
 */
public class Controller implements Initializable {
    private static final String NODESDMPSRC = "./res/nodes.dmp";
    private static final String NAMESDMPSRC = "./res/names.dmp";
    private static TaxonTree taxonTree;

    /**
     * initializes all required files
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //build the tree
        setUpRequiredFiles();
        TreeParser treeParser = new TreeParser();
        treeParser.parseTree(NODESDMPSRC, NAMESDMPSRC);
        taxonTree = treeParser.getTaxonTree();
    }

    /**
     * checks whether nodes.dmp and names.dmp exist
     * if not it downloads the files and puts them into the correct place
     */
    public static void setUpRequiredFiles() {
        File nodesDmp = new File(NODESDMPSRC);
        File namesDmp = new File(NAMESDMPSRC);

        //if files do NOT exist or they are a directory
        //-> download the files
        if (!(nodesDmp.exists() && namesDmp.exists() && !nodesDmp.isDirectory() && !namesDmp.exists())) {
            DownloadNodesAndNameDMPFiles.downloadNamesNodesDMPandUnzip();
        }

    }
}
