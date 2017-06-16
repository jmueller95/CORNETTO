package UI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.TaxonTree;
import treeParser.TreeParser;
import util.DownloadFilesHelper.DownloadNodesAndNameDMPFiles;

import javax.swing.plaf.FileChooserUI;
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

    //Elements of the GUI
    @FXML
    private Label leftLabel;

    /**
     * initializes all required files
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //build the tree
        System.out.println("Downloading files...");
        setUpRequiredFiles();
        System.out.println("Parsing tree...");
        TreeParser treeParser = new TreeParser();
        treeParser.parseTree(NODESDMPSRC, NAMESDMPSRC);
        taxonTree = treeParser.getTaxonTree();
    }

    /**
     * checks whether nodes.dmp and names.dmp exist
     * if not it downloads the files and puts them into the correct place
     */
    //TODO: Place this method on the outside of the Controller class to keep controller clean
    public static void setUpRequiredFiles() {
        File nodesDmp = new File(NODESDMPSRC), namesDmp = new File(NAMESDMPSRC);

        //if files do NOT exist or they are a directory
        //-> download the files
        if (!(nodesDmp.exists() && namesDmp.exists() && !nodesDmp.isDirectory() && !namesDmp.exists())) {
            DownloadNodesAndNameDMPFiles.downloadNamesNodesDMPandUnzip();
        }
    }

    @FXML
    /**
     * Opens a file chooser
     */
    public void handleOpenFile() {
        File selectedFile = new FileChooser().showOpenDialog(null);

        boolean isFileFound = selectedFile != null;
        leftLabel.setText(isFileFound ? selectedFile.getName() : "No such file found.");
    }

    /**
     * Exits the program
     */
    @FXML
    public void handleExit(){
        //TODO: Implement asking the user for saving the progress
        System.exit(0);
    }
}
