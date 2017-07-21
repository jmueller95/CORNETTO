package UI;


import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.NumberStringConverter;
import main.GlobalConstants;
import main.UserSettings;
import model.AnalysisData;
import model.LoadedData;
import model.Sample;
import sampleParser.BiomV1Parser;
import sampleParser.ReadName2TaxIdCSVParser;
import sampleParser.TaxonId2CountCSVParser;
import util.DownloadNodesAndNameDMPFiles;
import view.MyEdgeView;
import view.MyGraphView;
import view.MyVertexView;
import view.ViewPane;

import javax.jws.soap.SOAPBinding;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static main.Main.getPrimaryStage;

public class MainStageController implements Initializable {

    private static Stage optionsStage;

    private static final int MAX_WIDTH_OF_SIDEPANES = 220;

    private enum FileType {taxonId2Count, readName2TaxonId, biom}

    private ArrayList<String> openFiles;

    public static boolean isMainViewMaximized = false;

    // alerts
    private Alert fileNotFoundAlert, confirmQuitAlert, aboutAlert, fileAlreadyLoadedAlert, wrongFileAlert, insufficientDataAlert;

    // FXML elements
    @FXML
    private AnchorPane leftPane, rightPane;

    @FXML
    private Tab mainViewTab;

    @FXML
    private Label leftLabel;

    @FXML
    private TreeView<String> treeViewFiles;

    @FXML
    private Accordion preferencesAccordion;

    //Buttons
    @FXML
    private RadioButton collapseAllButton;

    @FXML
    private Button startAnalysisButton;


    /** FILTER OPTION ELEMENTS **/
    @FXML
    private ChoiceBox<String> rankChoiceBox;

    //List of possible choices of the choice box
    ObservableList<String> ranksList = FXCollections.observableArrayList("Domain", "Kingdom", "Phylum", "Class",
            "Order", "Family","Genus", "Species");

    @FXML
    private Slider minCorrelationSlider;

    @FXML
    private TextField minCorrelationText;

    @FXML
    private Slider maxCorrelationSlider;

    @FXML
    private TextField maxCorrelationText;

    @FXML
    private Slider maxPValueSlider;

    @FXML
    private TextField maxPValueText;

    @FXML
    private Slider minFrequencySlider;

    @FXML
    private TextField minFrequencyText;

    @FXML
    private Slider maxFrequencySlider;

    @FXML
    private TextField maxFrequencyText;


    @FXML
    private CheckBox useSelectedCheckBox;


    /** STARTUP PANE ELEMENTS **/
    @FXML
    private Label startupLabel;

    @FXML
    private ProgressIndicator startupSpinner;


    /** STATUS FOOTER ELEMENTS **/
    @FXML
    private Label statusRightLabel;


    /** GRAPH VIEW SETTING ELEMENTS **/
    @FXML
    private Slider sliderNodeRadius;

    @FXML
    private Slider sliderEdgeWidth;


    /**
     * Initializes every needed service
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startTreePreloadService();
        initializeTreeView();
        //initializeTextAreaDetails();
        initializeAccordion();
        initializeCollapseAllButton();
        //initializeMaxCountSlider();
        initializeButtonsOnTheRightPane();
        initializeRankChoiceBox();
        initializeSliderBindings();
        //preload settings
        UserSettings.addUserSettings();
        //SaveAndLoadOptions.loadSettings();

    }




    @FXML
    /**
     * Should be called when the user clicks a button to analyze the loaded samples and display the graphview
     * Creates correlation data, creates the internal graph, applies default filter, displays the graph
     */
    public void startAnalysis() {
        startAnalysisButton.setDisable(true);
        System.out.println("Performing analysis at level: " + AnalysisData.getLevel_of_analysis());
        boolean isAnalysisSuccessful = AnalysisData.performCorrelationAnalysis(LoadedData.getSamples());
        if(isAnalysisSuccessful) {
            LoadedData.createGraph();
            //Default values: 0.5<correlation<1, pValue<0.1
            LoadedData.getTaxonGraph().filterTaxa(
                    LoadedData.getSamples(), 1, 0.5, 0.1, AnalysisData.getLevel_of_analysis());
            System.out.println("Taxa filtered after " + AnalysisData.getLevel_of_analysis());
            displayGraph(LoadedData.getTaxonGraph());
        }else{//The analysis couldn't be done because of insufficient data
            showInsufficientDataAlert();
        }

    }

    private void displayGraph(MyGraph<MyVertex, MyEdge> taxonGraph) {
        MyGraphView graphView = new MyGraphView(taxonGraph);
        ViewPane viewPane = new ViewPane(graphView);

        // Bind node hover status text
        statusRightLabel.textProperty().bind(viewPane.hoverInfo);
        // Bind Node Radius Slider
        for (Node node : graphView.getMyVertexViewGroup().getChildren()) {
            ((MyVertexView) node).getRadiusProperty().bind(sliderNodeRadius.valueProperty());
        }
        // Bind Edge Width Slider
        for (Node node : graphView.getMyEdgeViewGroup().getChildren()) {
            ((MyEdgeView) node).getWidthProperty().bind(sliderEdgeWidth.valueProperty());
        }
        mainViewTab.setContent(viewPane);
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
            //textAreaDetails.setText("");
        }
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

    @FXML
    public void toggleMainView() {
        if (!isMainViewMaximized) {
            setPanesWidth(0);
            isMainViewMaximized = true;
        } else {
            setPanesWidth(MAX_WIDTH_OF_SIDEPANES);
            isMainViewMaximized = false;
        }
    }

    //SPECIALIZED METHODS

    private void setPanesWidth(int width) {
        leftPane.setMaxWidth(width);
        rightPane.setMaxWidth(width);
        leftPane.setMinWidth(width);
        rightPane.setMinWidth(width);
    }

    private void openFiles(FileType fileType) {
        FileChooser fileChooser = new FileChooser();
        String fileChooserTitle = "Load from ";

        if (UserSettings.isDefaultDirectoryLocation) {
            setDefaultOpenDirectory(fileChooser);
        } else {
            fileChooser.setInitialDirectory(UserSettings.defaultFilechooserLocation);
        }

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

        //DEBUG:
        fileChooser.setInitialDirectory(UserSettings.defaultFilechooserLocation);


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
        activateButtonsOnTheRightPane();
    }

    private void addBiomFileToTreeView(File file) {
        BiomV1Parser biomV1Parser = new BiomV1Parser(TreePreloadService.taxonTree);

        ArrayList<Sample> samples;

        samples = biomV1Parser.parse(file.getAbsolutePath());

        LoadedData.addSamplesToDatabase(samples, treeViewFiles);
        activateFilterOptions();
        activateButtonsOnTheRightPane();
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
        activateButtonsOnTheRightPane();
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
            //textAreaDetails.setText("");
            /*if (!treeItem.isLeaf()) {
                for (TreeItem<String> child : treeItem.getChildren()) {
                    textAreaDetails.appendText(child.getValue() + "\n");
                }
            } else {*/
            //textAreaDetails.appendText(treeItem.getValue() + "\n");
            //}
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

    /**
     * Activates the filter options after a file is loaded
     */
    private void activateFilterOptions() {
        //MaxCountSlider
//        initializeMaxCountSlider();
    }

    /**
     * Updates the maxCountText element
     */
//    public void updateMaxCountText() {
//        maxCountText.setText("Max count: " + (int) maxCountSlider.getValue());
//    }

    //INITIALIZATIONS

    /**
     * Starts the tree preload service
     */
    private void startTreePreloadService() {
        TreePreloadService treePreloadService = new TreePreloadService();
        treePreloadService.setOnSucceeded(e -> startupSpinner.setProgress(100));
        startupLabel.textProperty().bind(treePreloadService.messageProperty());
        treePreloadService.start();

    }

    /**
     * Initializes the tree view on left pane
     */
    private void initializeTreeView() {
        treeViewFiles.setRoot(new TreeItem<>("root"));
        treeViewFiles.setShowRoot(false);
    }

    /**
     * Summarizes the initialization of the buttons on the right pane
     */
    private void initializeButtonsOnTheRightPane() {
        initializeStartAnalysisButton();
        initializeSplitMenuButton();
        initializeUseSelectedCheckBox();
    }

    /**
     * Initializes the accordion on the right pane
     */
    private void initializeAccordion() {
        preferencesAccordion.setExpandedPane(preferencesAccordion.getPanes().get(0));
    }



    /**
     * Activates the buttons on the right pane
     */
    private void activateButtonsOnTheRightPane() {
        rankChoiceBox.setDisable(false);
    }

    /**
     * Initializes the text area on the right pane
     */
    private void initializeTextAreaDetails() {
        //textAreaDetails.setEditable(false);
    }

    /**
     * Initializes the collapse all button on the left pane
     */
    private void initializeCollapseAllButton() {
        collapseAllButton.setSelected(false);
    }

    /**
     * Initialize the use selected check box on the right pane
     */
    private void initializeUseSelectedCheckBox() {
        useSelectedCheckBox.setTooltip(new Tooltip("Use only selected Taxa"));
    }

    /**
     * Initializes the maxCount slider on the middle pane
     */
//    private void initializeMaxCountSlider() {
//        maxCountSlider.setMajorTickUnit(1);
//        maxCountSlider.setMinorTickCount(1);
//        maxCountSlider.setSnapToTicks(true);
//        if (treeViewFiles.getRoot().getChildren().isEmpty()) {
//            maxCountSlider.setDisable(true);
//        } else {
//            maxCountSlider.setDisable(false);
//        }
//        //maxCountSlider.setValue(maxCountSlider.getMax());
//    }

    /**
     * Initializes the start analysis button on the right pane
     */
    private void initializeStartAnalysisButton() {
        startAnalysisButton.setDisable(true);
    }

    /**
     * Initializes the split menu button on the right pane
     */
    private void initializeSplitMenuButton() {
        rankChoiceBox.setDisable(true);
    }

    /**
     * Initializes the rank selection toggle group and adds a listener to the rank selection
     */
    private void initializeRankChoiceBox() {
        rankChoiceBox.setItems(ranksList);
        rankChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                AnalysisData.setLevel_of_analysis(newValue.toLowerCase());
                startAnalysisButton.setDisable(false);
            }
        });
    }

    private void initializeSliderBindings(){
        //Since the slider value property is double and the text field property is a string, we need to convert them
        //Defining own class to avoid exceptions
        class MyNumberStringConverter extends NumberStringConverter{
            @Override
            public Number fromString(String value) {
                 try {
                     return super.fromString(value);
                 }catch(RuntimeException ex){
                     return 0;
                 }
            }
        }
        StringConverter<Number> converter = new MyNumberStringConverter();
        //Bind every slider to its corresponding text field and vice versa
        Bindings.bindBidirectional(minCorrelationText.textProperty(),minCorrelationSlider.valueProperty(),converter);
        Bindings.bindBidirectional(maxCorrelationText.textProperty(),maxCorrelationSlider.valueProperty(),converter);
        Bindings.bindBidirectional(maxPValueText.textProperty(),maxPValueSlider.valueProperty(),converter);
        Bindings.bindBidirectional(minFrequencyText.textProperty(),minFrequencySlider.valueProperty(),converter);
        Bindings.bindBidirectional(maxFrequencyText.textProperty(),maxFrequencySlider.valueProperty(),converter);
        //Bind the internal filter properties to the slider values
        AnalysisData.minCorrelationProperty().bind(minCorrelationSlider.valueProperty());
        AnalysisData.maxCorrelationProperty().bind(maxCorrelationSlider.valueProperty());
        AnalysisData.maxPValueProperty().bind(maxPValueSlider.valueProperty());
        AnalysisData.minFrequencyProperty().bind(minFrequencySlider.valueProperty());
        AnalysisData.maxFrequencyProperty().bind(maxFrequencySlider.valueProperty());




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
     * Prompts an alert telling the user that the chosen data is not sufficient for an analysis
     */
    private void showInsufficientDataAlert(){
        insufficientDataAlert = new Alert(Alert.AlertType.ERROR);
        insufficientDataAlert.setTitle("Insufficient Data for Analysis");
        insufficientDataAlert.setHeaderText("Maybe you're being to general, try choosing a more specific rank!");
        insufficientDataAlert.show();
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
     *
     */ private void optionsButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(new URL("file:" + new File("").getCanonicalPath().concat("/src/UI/optionsGui.fxml")));
            Parent root = fxmlLoader.load();
            this.optionsStage = new Stage();
            optionsStage.setTitle("Options");
            Scene optionsScene = new Scene(root, 800, 500);
            optionsStage.setScene(optionsScene);
            optionsScene.getStylesheets().add(GlobalConstants.DARKTHEME);
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

    public static Stage getOptionsStage() {
        return optionsStage;
    }
}
