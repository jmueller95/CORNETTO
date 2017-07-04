package UI;


import javafx.application.Platform;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;
import model.LoadedData;
import model.Sample;
import sampleParser.BiomV1Parser;
import sampleParser.ReadName2TaxIdCSVParser;
import sampleParser.TaxonId2CountCSVParser;
import util.DownloadNodesAndNameDMPFiles;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static main.Main.getPrimaryStage;

public class MainStageController implements Initializable {
    public static final String NODES_DMP_SRC = "./res/nodes.dmp";
    public static final String NAMES_DMP_SRC = "./res/names.dmp";

    private enum FileType {taxonId2Count, readName2TaxonId, biom}

    private ArrayList<String> openFiles;

    // alerts
    private Alert fileNotFoundAlert, confirmQuitAlert, aboutAlert, fileAlreadyLoadedAlert, wrongFileAlert;

    // FXML elements
    @FXML
    private Label leftLabel;

    @FXML
    private TreeView<String> treeViewFiles;

    @FXML
    private TextArea textAreaDetails;

    @FXML
    public ProgressBar progressBar;

    @FXML
    private RadioButton collapseAllButton;

    //Filter items
    @FXML
    private Slider maxCountSlider;

    @FXML private Text maxCountText;

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
        initializeTreeView();
        initializeTextAreaDetails();
        initializeCollapseAllButton();
        initializeMaxCountSlider();
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
            LoadedData.closeProject(treeViewFiles);
            textAreaDetails.setText("");
            maxCountSlider.setDisable(true);
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
     * quitButton
     */
    @FXML
    public void quit() {
        confirmQuit();
    }


    //SPECIALIZED METHODS

    private void openFiles(FileType fileType) {
        FileChooser fileChooser = new FileChooser();
        String fileChooserTitle = "Load from ";

        setDefaultOpenDirectory(fileChooser);
        switch (fileType) {
            case taxonId2Count:
                fileChooser.setTitle(fileChooserTitle + "taxonId2Count file");
                break;
            case readName2TaxonId:
                fileChooser.setTitle(fileChooserTitle + "readName2TaxonId file");
                break;
            case biom:
                fileChooser.setTitle(fileChooserTitle + "biom file");
                break;
        }

        //Choose the file / files
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null) {
            ArrayList<String> namesOfAlreadyLoadedFiles = new ArrayList<>();
            for (File file : selectedFiles) {
                String foundFileName = file.getName();
                if (openFiles != null && openFiles.contains(foundFileName)) {
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
                }
                //Maybe multiple at once?
                //verifyOpenedFile(selectedFile);
            }
            if (namesOfAlreadyLoadedFiles.size() != 0) {
                showFileAlreadyLoadedAlert(namesOfAlreadyLoadedFiles);
            }
        }
    }

    private void addReadName2TaxonIdFileToTreeView(File file) {
        ReadName2TaxIdCSVParser readName2TaxIdCSVParser = new ReadName2TaxIdCSVParser(TreePreloadService.taxonTree);

        ArrayList<Sample> samples;

        try {
            samples = readName2TaxIdCSVParser.parse(file.getAbsolutePath());
        } catch (IOException e) {
            showWrongFileAlert();
            return;
        }

        LoadedData.addSamplesToDatabase(samples, treeViewFiles);
        activateFilterOptions();
    }

    private void addBiomFileToTreeView(File file) {
        BiomV1Parser biomV1Parser = new BiomV1Parser(TreePreloadService.taxonTree);

        ArrayList<Sample> samples;

        samples = biomV1Parser.parse(file.getAbsolutePath());

        LoadedData.addSamplesToDatabase(samples, treeViewFiles);
        activateFilterOptions();
    }

    private void addId2CountFileToTreeView(File file) {
        TaxonId2CountCSVParser taxonId2CountCSVParser = new TaxonId2CountCSVParser(TreePreloadService.taxonTree);

        ArrayList<Sample> samples;

        try {
            samples = taxonId2CountCSVParser.parse(file.getAbsolutePath());
        } catch (IOException e) {
            showWrongFileAlert();
            return;
        }

        LoadedData.addSamplesToDatabase(samples, treeViewFiles);
        activateFilterOptions();
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
     *
     * Shows the details of the selected taxon
     */ public void selectTaxon() {
        TreeItem<String> treeItem = treeViewFiles.getSelectionModel().getSelectedItem();

        if (treeItem != null) {
            textAreaDetails.setText("");
            if (!treeItem.isLeaf()) {
                for (TreeItem<String> child : treeItem.getChildren()) {
                    textAreaDetails.appendText(child.getValue() + "\n");
                }
            } else {
                textAreaDetails.appendText(treeItem.getValue() + "\n");
            }
        }
    }

    @FXML
    /**
     * Collapses all nodes in the treeview element
     */
    public void collapseAll() {
        if (treeViewFiles.getRoot().getChildren().isEmpty()) {
            collapseAllButton.disarm();
            collapseAllButton.setSelected(false);
        } else {
            if (collapseAllButton.isSelected()) {
                for (TreeItem<String> treeItem : treeViewFiles.getRoot().getChildren()) {
                    treeItem.setExpanded(false);
                }
                collapseAllButton.disarm();
                collapseAllButton.setSelected(false);
            } else {
                for (TreeItem<String> treeItem : treeViewFiles.getRoot().getChildren()) {
                    treeItem.setExpanded(true);
                }
                collapseAllButton.setSelected(true);
                collapseAllButton.arm();
            }
        }
    }

    @FXML
    public void applyMaxCountFilter() {
        LoadedData.filterTaxaAfterCount(treeViewFiles, (int) maxCountSlider.getValue());
        //System.out.println("Found value: " + (int) maxCountSlider.getValue());
        for (TreeItem<String> treeItem : treeViewFiles.getRoot().getChildren()) {
            treeItem.setExpanded(true);
        }
    }

    /**
     * Activates the filter options after a file is loaded
     */
    private void activateFilterOptions() {
        //MaxCountSlider
        initializeMaxCountSlider();
    }

    /**
     * Updates the maxCountText element
     */
    public void updateMaxCountText() {
        maxCountText.setText("Max count: " + (int) maxCountSlider.getValue());
    }

    //INITIALIZATIONS

    /**
     * Initializes the tree view on left pane
     */
    private void initializeTreeView() {
        treeViewFiles.setRoot(new TreeItem<>("root"));
        treeViewFiles.setShowRoot(false);
    }

    /**
     * Initializes the text area on the right pane
     */
    private void initializeTextAreaDetails() {
        textAreaDetails.setEditable(false);
    }

    /**
     * Initializes the collapse all button on the left pane
     */
    private void initializeCollapseAllButton() {
        collapseAllButton.setSelected(false);
    }

    /**
     * Initializes the maxCount slider on the middle pane
     */
    private void initializeMaxCountSlider() {
        maxCountSlider.setMajorTickUnit(1);
        maxCountSlider.setMinorTickCount(1);
        maxCountSlider.setSnapToTicks(true);
        if(treeViewFiles.getRoot().getChildren().isEmpty()) {
            maxCountSlider.setDisable(true);
            maxCountText.setText("Max count: ");
        } else {
            maxCountSlider.setDisable(false);
            maxCountSlider.setMin(LoadedData.getMinCount() == 0 ? 1 : LoadedData.getMinCount());
            maxCountSlider.setMax(LoadedData.getMaxCount());
            maxCountText.setText("Max count: " + maxCountSlider.getMax());
        }
        maxCountSlider.setValue(maxCountSlider.getMax());
    }
    //ALERTS

    /**
     * creates the file not found alert box
     */
    private void fileNotFoundAlertBox() {
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
     *
     * Shows information about the software.
     */ private void showAboutAlert() {
        String information = String.format("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." + " Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.");
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
        StringBuilder namesOfFileAlreadyLoaded = new StringBuilder();

        for (String name : fileNames) {
            namesOfFileAlreadyLoaded.append(name).append(fileNames.size() == 1 || fileNames.get(fileNames.size()).equals(name) ? "" : ", ");
        }
        String fileAlreadyLoaded = "The files '" + namesOfFileAlreadyLoaded + "' is already loaded in your project.";
        fileAlreadyLoadedAlert = new Alert(Alert.AlertType.ERROR);
        fileAlreadyLoadedAlert.setTitle("File not loaded.");
        fileAlreadyLoadedAlert.setContentText(fileAlreadyLoaded);
        fileAlreadyLoadedAlert.show();
    }

    /**
     * method for the quit button
     * opens an alert box
     * asks whether to save/quit/continue running the program
     */
    private void confirmQuit() {
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
        } else {
            confirmQuitAlert.close();
        }
    }

    @FXML
    /**
     * runs when the optionsButton is clicked
     * opens the Options stage
     */ private void optionsButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(new URL("file:" + new File("").getCanonicalPath().concat("/src/UI/optionsGui.fxml")));
            Parent root = fxmlLoader.load();
            root.getStylesheets().add("/UI/optionsStyle.css/");
            Stage optionsStage = new Stage();
            optionsStage.setTitle("Options");
            optionsStage.setScene(new Scene(root, 800, 500));
            optionsStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * handler for Window Events
     * opens an alert which asks whether to quit or not
     * use when setting handlers of the X button
     */
    public EventHandler<WindowEvent> confirmCloseEventHandler = (WindowEvent event) -> {
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
            Platform.exit();
        } else {
            event.consume();
        }
    };
}
