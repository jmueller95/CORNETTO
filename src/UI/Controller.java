package UI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import model.Sample;
import model.TaxonTree;
import sampleParser.ReadName2TaxIdCSVParser;
import sampleParser.TaxonId2CountCSVParser;
import treeParser.TreeParser;
import util.DownloadFilesHelper.DownloadNodesAndNameDMPFiles;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Zeth on 15.06.2017.
 */
public class Controller implements Initializable {
    private static final String NODESDMPSRC = "./res/nodes.dmp";
    private static final String NAMESDMPSRC = "./res/names.dmp";
    private static TaxonTree taxonTree;

    private static int id = 1;

    private ArrayList<String> openFiles;

    //Elements of the GUI
    @FXML
    private Label leftLabel;

    @FXML
    private TreeView<String> treeViewFiles;

    @FXML
    private TextArea textAreaDetails;

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
        initializeTreeView();
        textAreaDetails.setEditable(false);
        openFiles = new ArrayList<>();
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

    /**
     * Initializes the tree view on left pane
     */
    public void initializeTreeView() {
        treeViewFiles.setRoot(new TreeItem<>("root"));
        treeViewFiles.setShowRoot(false);
    }

    @FXML
    /**
     * Opens a file chooser and gives the user the possibility to select a file
     */
    public void handleOpenFile() {
        File selectedFile = new FileChooser().showOpenDialog(null);

        boolean isFileFound = selectedFile != null;
        //leftLabel.setText(isFileFound ? selectedFile.getName() : "No such file found.");
        if (isFileFound) {
            addFileToTreeView(selectedFile);
        }
    }

    @FXML
    /**
     * Closes the current project and empties the tree view
     */
    public void handleCloseProcject() {
        if (!treeViewFiles.getRoot().getChildren().isEmpty()) {
            treeViewFiles.getRoot().getChildren().remove(0, treeViewFiles.getRoot().getChildren().size());
            textAreaDetails.setText("");
            openFiles = new ArrayList<>();
        }
    }

    /**
     * Adds and displays the file name and a placeholder id for the file name in the treeview gui element
     *
     * @param file
     */
    public void addFileToTreeView(File file) {

        if (file.getName().endsWith(".txt") && !openFiles.contains(file.getName())) {

            openFiles.add(file.getName());

            TaxonId2CountCSVParser taxonId2CountCSVParser = new TaxonId2CountCSVParser(taxonTree);
            ReadName2TaxIdCSVParser readName2TaxIdCSVParser = new ReadName2TaxIdCSVParser(taxonTree);

            ArrayList<Sample> sampleList = null;
            try {
                try {
                    sampleList = taxonId2CountCSVParser.parse(file.getAbsolutePath());
                } catch (IllegalArgumentException e) {
                    sampleList = readName2TaxIdCSVParser.parse(file.getAbsolutePath());
                }
            } catch (IOException e) {
                System.out.println("File not found.");
            }

            TreeItem<String> newRoot, newRootCount;

            for (Sample sample : sampleList) {
                newRoot = new TreeItem<>("Id: " + id++);
                newRootCount = new TreeItem<>("Count: " + sample.getClass().toString());
                newRoot.getChildren().addAll(newRootCount);
                treeViewFiles.getRoot().getChildren().add(newRoot);
            }
        }
    }

    @FXML
    /**
     * Shows the details of the selected taxon
     */
    public void handleTaxonSelected() {
        TreeItem<String> treeItem;
        if ((treeItem = treeViewFiles.getSelectionModel().getSelectedItem()) != null) {
            textAreaDetails.setText("");
            textAreaDetails.appendText(treeItem.getValue() + "\n");
            for (TreeItem<String> child : treeItem.getChildren()) {
                textAreaDetails.appendText(child.getValue() + "\n");
            }
        }
    }

    /**
     * Exits the program
     */
    @FXML
    public void handleExit() {
        //TODO: Implement asking the user for saving the progress
        System.exit(0);
    }
}
