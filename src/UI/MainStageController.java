package UI;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Sample;
import model.TaxonTree;
import sampleParser.BiomV1Parser;
import sampleParser.ReadName2TaxIdCSVParser;
import sampleParser.TaxonId2CountCSVParser;
import util.DownloadFilesHelper.DownloadNodesAndNameDMPFiles;

import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static Main.Main.getPrimaryStage;

public class MainStageController implements Initializable {
    //The names of variables declared class constants and of ANSI constants should be all uppercase with words separated by underscores ("_"). https://softwareengineering.stackexchange.com/questions/252243/naming-convention-final-fields-not-static
    public static final String NODES_DMP_SRC = "./res/nodes.dmp";
    public static final String NAMES_DMP_SRC = "./res/names.dmp";

    private static TaxonTree taxonTree;

    private static int id = 1;

    private enum FileType {taxonId2Count, readName2TaxonId, biom}

    ;

    public ArrayList<String> openFiles;

    //Elements of the GUI
    //Elements of the GUI

    //I did not find those in the gluon scenebuilder?
    private Alert fileNotFoundAlert, confirmQuitAlert, aboutAlert, fileAlreadyLoadedAlert, wrongFileAlert;

    // FXML elements
    @FXML
    private Label leftLabel;

    @FXML
    private TreeView<String> treeViewFiles;

    @FXML
    public TextArea textAreaDetails;

    @FXML
    public ProgressBar progressBar;

    /**
     * initializes all required files
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreePreloadService treePreloadService = new TreePreloadService();
        treePreloadService.start();
    }

    /**
     * checks whether nodes.dmp and names.dmp exist
     * if not it downloads the files and puts them into the correct place
     */
    //TODO: Place this method on the outside of the MainStageController class to keep controller clean
    public static void setUpRequiredFiles() {
        File nodesDmp = new File(NODES_DMP_SRC), namesDmp = new File(NAMES_DMP_SRC);

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

    //FILE methods
    @FXML
    /**
     * opens a file chooser and gives the user the possibility to select a file
     * file chooser default location is where save states are
     */ public void openRecentFile() {
        /*openFileWindow();*/
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

    @FXML
    public void openId2CountFiles() {
        openFiles(FileType.taxonId2Count);
    }

    @FXML
    public void openReadName2TaxonIdFiles() {
        openFiles(FileType.readName2TaxonId);
    }

    @FXML
    public void openBiomFiles() {
        openFiles(FileType.taxonId2Count);
    }

    /**
     * Exits the program
     */
    @FXML
    public void quit() {
        confirmQuit();
    }


    //SPECIALIZED METHODS

    private void openFiles(FileType fileType) {
        FileChooser fileChooser = new FileChooser();

        setDefaultOpenDirectory(fileChooser);

        //Choose the file
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null) {
            ArrayList<String> namesOfAlreadyLoadedFiles = new ArrayList<>();
            for (File file : selectedFiles) {
                String foundFileName = file.getName();
                if (openFiles.contains(foundFileName)) {
                    namesOfAlreadyLoadedFiles.add(foundFileName);
                } else {
                    switch (fileType) {
                        case taxonId2Count:
                            addId2CountFileToTreeView(file);
                            break;
                        case readName2TaxonId:
                            addReadName2TaxonIdFileToTreeView(file);
                            break;
                        case biom:
                            addBiomFileToTreeView(file);
                            break;
                        default:
                            //Will never happen
                            break;
                    }
                    showFileAlreadyLoadedAlert(namesOfAlreadyLoadedFiles);
                }
                //Maybe multiple at once?
                //verifyOpenedFile(selectedFile);
            }

        }
    }

    private void addReadName2TaxonIdFileToTreeView(File file) {

        ReadName2TaxIdCSVParser readName2TaxIdCSVParser = new ReadName2TaxIdCSVParser(taxonTree);

        ArrayList<Sample> samples = null;

        try {
            samples = readName2TaxIdCSVParser.parse(file.getAbsolutePath());
        } catch (IOException e) {
            showWrongFileAlert();
            return;
        }

        addSamplesToTreeView(samples);
    }

    private void addBiomFileToTreeView(File file) {

        BiomV1Parser biomV1Parser = new BiomV1Parser(taxonTree);

        ArrayList<Sample> samples = null;

        samples = biomV1Parser.parse(file.getAbsolutePath());

        addSamplesToTreeView(samples);
    }

    private void addId2CountFileToTreeView(File file) {

        TaxonId2CountCSVParser taxonId2CountCSVParser = new TaxonId2CountCSVParser(taxonTree);

        ArrayList<Sample> samples = null;

        try {
            samples = taxonId2CountCSVParser.parse(file.getAbsolutePath());
        } catch (IOException e) {
            showWrongFileAlert();
            return;
        }

        addSamplesToTreeView(samples);
    }

    private void addSamplesToTreeView(ArrayList<Sample> samples) {
        TreeItem<String> newRoot, newRootCount;

        for (Sample sample : samples) {
            newRoot = new TreeItem<>("Id: " + id++);
            newRootCount = new TreeItem<>("Count: " + sample.getClass().toString());
            newRoot.getChildren().addAll(newRootCount);
            treeViewFiles.getRoot().getChildren().add(newRoot);
        }
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
            //addFileToTreeView(selectedFile);
        }
    }

    /**
     * sets the default directory for openings files
     *
     * @param fileChooser
     */
    private void setDefaultOpenDirectory(FileChooser fileChooser) {
        //Set to user directory or go to default if cannot access
        //TODO: osx?
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        fileChooser.setInitialDirectory(userDirectory);
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

    //ALERTS

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
     * Prompts an alert if the user tries to load a file that does not match the requirements.
     */
    //TODO: If multiple files are wrong, not every file should get its own alert.
    //Could also refactor and use the fileNotFoundAlert
    private void showWrongFileAlert() {
        wrongFileAlert = new Alert(Alert.AlertType.ERROR);
        wrongFileAlert.setTitle("File not loaded");
        wrongFileAlert.setHeaderText("Invalid file.");
        aboutAlert.show();
    }

    /**
     * Prompts an alert that the selected file is already part of the current project.
     */
    private void showFileAlreadyLoadedAlert(ArrayList<String> fileNames) {
        String namesOfFileAlreadyLoaded = "";

        for (String name : fileNames) {
            namesOfFileAlreadyLoaded += name + (fileNames.size() == 1 || fileNames.get(fileNames.size()).equals(name) ? "" : ", ");
        }
        String fileAlreadyLoaded = "The files '" + namesOfFileAlreadyLoaded + "' is already loaded in your project.";
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

        confirmQuitAlert.initModality(Modality.APPLICATION_MODAL);
        confirmQuitAlert.initOwner(getPrimaryStage());
        confirmQuitAlert.getButtonTypes().setAll(quitButton, saveAndQuitButton, cancelButton);

        Optional<ButtonType> result = confirmQuitAlert.showAndWait();
        if (result.get() == quitButton) {
            Platform.exit();
        } else if (result.get() == saveAndQuitButton) {
            confirmQuitAlert.close();
            //Stage stage = getPrimaryStage();
            //stage.show();
        } else {
            confirmQuitAlert.close();
            //Stage stage = getPrimaryStage();
            //stage.show();
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
