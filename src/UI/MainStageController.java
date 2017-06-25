package UI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Sample;
import model.TaxonTree;
import sampleParser.ReadName2TaxIdCSVParser;
import sampleParser.TaxonId2CountCSVParser;
import treeParser.TreeParser;
import util.DownloadFilesHelper.DownloadNodesAndNameDMPFiles;

import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.stage.Modality.*;

/**
 * Created by Zeth on 15.06.2017.
 */
public class MainStageController implements Initializable {
    private static final String NODESDMPSRC = "./res/nodes.dmp";
    private static final String NAMESDMPSRC = "./res/names.dmp";
    private static TaxonTree taxonTree;

    private static int id = 1;

    private ArrayList<String> openFiles;

    //Elements of the GUI

    //I did not find those in the gluon scenebuilder?
    private Alert fileNotFoundAlert, confirmQuitAlert, aboutAlert, fileAlreadyLoadedAlert;

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
    //TODO: Place this method on the outside of the MainStageController class to keep controller clean
    public static void setUpRequiredFiles() {
        File nodesDmp = new File(NODESDMPSRC), namesDmp = new File(NAMESDMPSRC);

        //if files do NOT exist or they are a directory
        //-> download the files
        if (!(nodesDmp.exists() && namesDmp.exists() && !nodesDmp.isDirectory() && !namesDmp.isDirectory())) {
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
     */ public void openFile() {
        openFileWindow("CSV and BIOM files", "*.csv", "*.biom");
    }


    @FXML
    /**
     * opens a file chooser and gives the user the possibility to select a file
     * file chooser default location is where save states are
     */ public void openRecentFile() {
        openFileWindow("CSV and BIOM files", "*.csv", "*.biom");
    }

    /**
     * verifies the opened file
     * should be not null and possible to add to the taxonView
     *
     * @param selectedFile
     */
    private void verifyOpenedFile(File selectedFile) {
        boolean isFileFound = selectedFile != null;
        if (!isFileFound) {
            fileNotFoundAlertBox();
        }
        //leftLabel.setText(isFileFound ? selectedFile.getName() : "No such file found.");
        if (isFileFound) {
            addFileToTreeView(selectedFile);
        }
    }

    /**
     * creates a openFileWindow
     * requires extensionFilters
     *
     * @param extensionFilterDescription
     * @param extensionFilterExtensions
     */
    private void openFileWindow(String extensionFilterDescription, String... extensionFilterExtensions) {
        FileChooser fileChooser = new FileChooser();

        //Extention filter
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(extensionFilterDescription, extensionFilterExtensions);
        fileChooser.getExtensionFilters().add(extensionFilter);

        setDefaultOpenDirectory(fileChooser);

        //Choose the file
        File selectedFile = fileChooser.showOpenDialog(null);

        verifyOpenedFile(selectedFile);
    }

    /**
     * sets the default directory for openings files
     *
     * @param fileChooser
     */
    private void setDefaultOpenDirectory(FileChooser fileChooser) {
        //Set to user directory or go to default if cannot access
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fileChooser.setInitialDirectory(userDirectory);
    }

    /**
     * creates the file not found alert box
     */
    public void fileNotFoundAlertBox() {
        fileNotFoundAlert = new Alert(Alert.AlertType.ERROR);
        fileNotFoundAlert.setTitle("File not found");
        fileNotFoundAlert.setHeaderText("File not found");
        fileNotFoundAlert.setContentText("Could not find the file you were looking for");

        //style the alert
        DialogPane dialogPane = fileNotFoundAlert.getDialogPane();
        dialogPane.getStylesheets().add("/UI/alertStyle.css");

        Exception fileNotFoundException = new FileNotFoundException("Could not find your selected file");

        //create expandable exception
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        fileNotFoundException.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was: ");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        fileNotFoundAlert.getDialogPane().setExpandableContent(expContent);

        fileNotFoundAlert.showAndWait();
    }

    @FXML
    /**
     * Closes the current project and empties the tree view
     */ public void closeProject() {
        if (!treeViewFiles.getRoot().getChildren().isEmpty()) {
            treeViewFiles.getRoot().getChildren().remove(0, treeViewFiles.getRoot().getChildren().size());
            textAreaDetails.setText("");
            openFiles = new ArrayList<>();
        }
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
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
        } else if (file.getName().endsWith(".txt") && openFiles.contains(file.getName())) {
            showFileAlreadyLoadedAlert();
        }
    }

    @FXML
    /**
     * Shows the details of the selected taxon
     */ public void selectTaxon() {
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
    public void quit() {
        confirmQuit();
    }

    @FXML
    /**
     * Shows information about the software.
     */ public void showAboutAlert() {
        String information = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in " + "reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " + "culpa qui officia deserunt mollit anim id est laborum.";
        Text text = new Text(information);
        text.setWrappingWidth(500);
        aboutAlert = new Alert(Alert.AlertType.INFORMATION);
        aboutAlert.setTitle("About NetWork Analysis Tool");
        aboutAlert.setHeaderText("What is the Network Analysis Tool?");
        aboutAlert.getDialogPane().setContent(text);
        aboutAlert.show();
    }

    /**
     * Prompts an alert that the selected file is already part of the current project.
     */
    private void showFileAlreadyLoadedAlert() {
        String fileAlreadyLoaded = "The selected file is already in your project.";
        fileAlreadyLoadedAlert = new Alert(Alert.AlertType.ERROR);
        fileAlreadyLoadedAlert.setTitle("File not loaded.");
        fileAlreadyLoadedAlert.setContentText(fileAlreadyLoaded);
        fileAlreadyLoadedAlert.show();
    }

    /**
     * method for the quit button
     * asks whether to save/quit/continue running the program
     */
    public void confirmQuit() {
        confirmQuitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmQuitAlert.setTitle("Remember to save your files!");
        confirmQuitAlert.setHeaderText("Quit?");
        confirmQuitAlert.setContentText("Do you really want to quit?");

        ButtonType quitButton = new ButtonType("Quit");
        ButtonType saveAndQuitButton = new ButtonType("Save and quit");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmQuitAlert.getButtonTypes().setAll(quitButton, saveAndQuitButton, cancelButton);

        Optional<ButtonType> result = confirmQuitAlert.showAndWait();
        if (result.get() == quitButton) {
            System.exit(0);
        } else if (result.get() == saveAndQuitButton) {
            //TODO SAVE AND QUIT
            ;
        } else {
            //continue with the program
            //user must have pressed cancel
        }
    }

    @FXML
    public void optionsButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(new URL("file:" + new File("").getCanonicalPath().concat("/src/UI/optionsGui.fxml")));
            Parent root = fxmlLoader.load();
            Stage optionsStage = new Stage();
            optionsStage.setTitle("Options");
            optionsStage.setScene(new Scene(root, 800, 500));
            optionsStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
