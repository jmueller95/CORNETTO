package UI;

import analysis.GraphAnalysis;
import analysis.SampleComparison;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import main.GlobalConstants;
import main.UserSettings;
import model.AnalysisData;
import model.LoadedData;
import model.Sample;
import model.TaxonNode;
import org.apache.commons.math3.linear.RealMatrix;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import sampleParser.BiomV1Parser;
import sampleParser.BiomV2Parser;
import sampleParser.ReadName2TaxIdCSVParser;
import sampleParser.TaxonId2CountCSVParser;
import util.SaveAndLoadOptions;
import view.*;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static main.Main.getPrimaryStage;
import static model.AnalysisData.*;

/**
 * <h1>This is the main GUI class. It implements most methods for the main stage.</h1>
 * <p>
 * Most of the functionality is channeled in this class. This is the class where all of the functionality comes
 * together. It contains the method for initializing all required Services and it implements all buttons in the main
 * stage.
 * </p>
 *
 * @see SpringAnimationService
 * @see ViewPane
 * @see MySpringLayout
 * @see MyColours
 * @see Palette
 *
 * @version This class should be divided into more classes which separate behaviour.
 */
public class MainStageController implements Initializable {
    //Default constants for the analysis sliders
    public static final double DEFAULT_POSITIVE_CORRELATION_LOW = 0.5;
    public static final int DEFAULT_POSITIVE_CORRELATION_HIGH = 1;
    public static final int DEFAULT_NEGATIVE_CORRELATION_LOW = -1;
    public static final double DEFAULT_NEGATIVE_CORRELATION_HIGH = -0.5;
    public static final double DEFAULT_MAX_P_VALUE_SLIDER = 0.05;
    public static final int DEFAULT_FREQUENCY_RANGE_SLIDER_LOW = 0;
    public static final int DEFAULT_FREQUENCY_RANGE_SLIDER_HIGH = 1;

    //Default constants for the Graph settings
    public static final int DEFAULT_ANIMATION_SPEED = 25;
    public static final double DEFAULT_SLIDER_EDGE_FORCE = 1.5;
    public static final int DEFAULT_NODE_REPULSION = 30;
    public static final double DEFAULT_SLIDER_STRECH_PARAMETER = 0.9;
    public static final int DEFAULT_SLIDER_NODE_RADIUS = 15;
    public static final int DEFAULT_SLIDER_EDGE_WIDTH = 5;
    public static final int DEFAULT_SLIDER_EDGE_LENGTH_LOW = 50;
    public static final int DEFAULT_SLIDER_EDGE_LENGTH_HIGH = 500;

    private static Stage optionsStage;
    private static Stage exportImagesStage;

    private ViewPane viewPane;

    private static final int MAX_WIDTH_OF_SIDEPANES = 220;

    public MainStageController() {
    }

    private enum FileType {taxonId2Count, readName2TaxonId, biomV1, biomV2, qiime}

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

    /**
     * BUTTON ELEMENTS
     */
    @FXML
    private RadioButton collapseAllButton;

    /**
     * FILTER OPTION ELEMENTS
     **/
    @FXML
    private ChoiceBox<String> rankChoiceBox;

    //List of possible choices of the choice box
    ObservableList<String> ranksList = FXCollections.observableArrayList("Domain", "Kingdom", "Phylum", "Class",
            "Order", "Family", "Genus", "Species");

    @FXML
    private RadioButton compareSelectedSamplesButton;

    @FXML
    private RadioButton pearsonCorrelationButton, spearmanCorrelationButton, kendallCorrelationButton;

    @FXML
    private TextField minPosCorrelationText, maxPosCorrelationText, minNegCorrelationText, maxNegCorrelationText;

    @FXML
    private Slider maxPValueSlider;

    @FXML
    private TextField maxPValueText;

    @FXML
    private TextField minFrequencyText;

    @FXML
    private TextField maxFrequencyText;

    @FXML
    private RangeSlider posCorrelationRangeSlider, negCorrelationRangeSlider, frequencyRangeSlider;

    /**
     * STARTUP PANE ELEMENTS
     **/
    @FXML
    private Label startupLabel;

    @FXML
    private ProgressIndicator startupSpinner;


    /**
     * STATUS FOOTER ELEMENTS
     **/
    @FXML
    private Label statusRightLabel;


    /**
     * GRAPH VIEW SETTING ELEMENTS
     **/
    @FXML
    private Slider sliderNodeRadius;

    @FXML
    private Slider sliderEdgeWidth;

    @FXML
    private RangeSlider sliderEdgeLength;

    @FXML
    private ToggleButton buttonPauseAnimation;

    @FXML
    private CheckBox checkAdvancedGraphSettings;

    @FXML
    private Label labelNodeRepulsion;

    @FXML
    private Slider sliderNodeRepulsion;

    @FXML
    private Label labelStretchParameter;

    @FXML
    private Slider sliderStretchParameter;

    @FXML
    private Label labelAnimationSpeed;

    @FXML
    private Slider sliderAnimationSpeed;

    @FXML
    private Label labelEdgeForce;

    @FXML
    private Slider sliderEdgeForce;

    @FXML
    private Button buttonResetGraphDefaults;

    @FXML
    private CheckBox showLabelsCheckBox;

    /**
     * COLOUR SETTINGS ELEMENTS
     */
    @FXML
    private RadioButton colourRadioNodeFix;

    @FXML
    private RadioButton colourRadioNodeParent;

    @FXML
    private RadioButton colourRadioNodeAlpha;

    @FXML
    private RadioButton colourRadioNodeFrequency;

    @FXML
    private ToggleGroup colourToggleNodes;

    @FXML
    private StackPane colourNodeComboContainer;

    ComboBox<Palette> nodeColourCombo = new ComboBox<>();


    @FXML
    private RadioButton colourRadioEdgeCorrelation;

    @FXML
    private RadioButton colourRadioEdgeDistance;

    @FXML
    private RadioButton colourRadioEdgePvalue;

    @FXML
    private ToggleGroup colourToggleEdges;

    @FXML
    private StackPane colourEdgeComboContainer;

    ComboBox<Palette> edgeColourCombo = new ComboBox<>();


    @FXML
    private Slider excludeFrequencySlider;

    @FXML
    private TextField excludeFrequencyText;

    /**
     * ANALYSIS PANE ELEMENTS
     */

    @FXML
    private AnchorPane analysisPane;

    @FXML
    private PieChart frequencyChart;

    @FXML
    private BarChart<String, Double> degreeDistributionChart;

    @FXML
    private TextArea graphStatText, dataStatText;

    @FXML
    private TextArea modularityText;

    /**
     * INFO PANE
     */
    @FXML
    private TextFlow infoTextFlow;

    @FXML
    private TextArea infoTextArea;

    @FXML
    private Button abundancePlotButton;

    /**
     * Initializes every needed service
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load FontAweSome
        GlyphFontRegistry.register(new FontAwesome(getClass().getResourceAsStream("/fonts/fontawesome-webfont.ttf")));

        startTreePreloadService();
        initializeAccordion();
        initializeRankChoiceBox();
        initializeGraphSettings();
        initializeAnalysisPane();
        initializeGraphAnalysis();
        initializeInfoPane();
        initializeBindings();
        initializeColorComboBox();
        initializeButtonsOnLeftPane();
        //preload settings
        SaveAndLoadOptions.loadSettings();

        //Display the info text in the bottom left pane
        displayInfoText();
    }


    @FXML
    /**
     * Should be called when the user clicks a button to analyze the loaded samples and display the graphview
     * Creates correlation data, creates the internal graph, applies default filter, displays the graph
     */
    public void startAnalysis() {
        for (Sample sample : LoadedData.getSamplesToAnalyze()) {
            sample.filterTaxaPrimary();
        }

        String correlationType = "";
        if (pearsonCorrelationButton.isSelected())
            correlationType = "pearson";
        else if (spearmanCorrelationButton.isSelected())
            correlationType = "spearman";
        else if (kendallCorrelationButton.isSelected())
            correlationType = "kendall";

        boolean isAnalysisSuccessful = AnalysisData.performCorrelationAnalysis(new ArrayList<>(LoadedData.getSamplesToAnalyze()), correlationType);
        if (isAnalysisSuccessful) {
            LoadedData.createGraph();
            LoadedData.getTaxonGraph().filterEdges();
            LoadedData.getTaxonGraph().filterVertices();
            displayGraph(LoadedData.getTaxonGraph());
            displayAnalysisTextsAndGraphs();
            performGraphAnalysis();
            displayGraphAnalysis();
            displayInfoText();
            setHubsInView();
        } else {//The analysis couldn't be done because of insufficient data
            showInsufficientDataAlert();
        }
    }

    @FXML
    public void showAllSamples() {
        if (treeViewFiles.getRoot() != null && treeViewFiles.getRoot().getChildren().isEmpty()) {
            System.out.println("I will show every node.");
        }
    }

    @FXML
    public void showNoSamples() {
        if (treeViewFiles.getRoot() != null && !treeViewFiles.getRoot().getChildren().isEmpty()) {
            System.out.println("I will hide every node.");
            treeViewFiles.getRoot().getChildren().remove(0, treeViewFiles.getRoot().getChildren().size());
        }
    }

    @FXML
    public void showReverseSamples() {

    }

    /**
     * chooses which text to display on the bottom left pane
     * TODO: This isn't called every time it should be, add some more listeners!
     */
    public void displayInfoText() {
        String infoText = "";
        if (LoadedData.getSamplesToAnalyze() == null || LoadedData.getSamples().size() < 3) {
            infoText = "Please import at least 3 samples \nto begin correlation analysis!";
        } else if (compareSelectedSamplesButton.isSelected() && LoadedData.getSelectedSamples().size() < 3) {
            infoText = "If you want to analyse selected \nsamples only, please select \nat least 3 samples!";
        } else if (rankChoiceBox.getValue() == null) {
            infoText = "Choose a rank to display the graph!";
        } else if (LoadedData.getGraphView() != null && LoadedData.getGraphView().getSelectionModel().getSelectedItems().size() > 1) {
            StringBuilder builder = new StringBuilder("Selected Taxa:\n");
            ObservableList selectedItems = LoadedData.getGraphView().getSelectionModel().getSelectedItems();
            for (Object selectedItem : selectedItems) {
                MyVertex vertex = (MyVertex) selectedItem;
                builder.append(vertex.getTaxonName());
                builder.append("\n");
            }
            infoText = builder.toString();
        } else if (LoadedData.getGraphView() != null && LoadedData.getGraphView().getSelectionModel().getSelectedItems().size() == 1) {
            MyVertex selectedVertex = (MyVertex) LoadedData.getGraphView().getSelectionModel().getSelectedItems().get(0);
            GraphAnalysis analysis = AnalysisData.getAnalysis();
            infoText = "Selected Taxon:\n" + selectedVertex.getTaxonName() + "\nID: " + selectedVertex.getTaxonNode().getTaxonId()
                    + "\nFrequency: " + String.format("%.3f", AnalysisData.getMaximumRelativeFrequencies().get(selectedVertex.getTaxonNode()))
                    + "\nNo. of visible edges: " + analysis.getNodeDegrees().get(selectedVertex.getTaxonNode());
        } else if (LoadedData.getGraphView() != null) {
            GraphAnalysis analysis = AnalysisData.getAnalysis();
            ;
            infoText = "Network Overview: \nNo. of visible taxa: " + analysis.getFilteredGraph().getVertices().size()
                    + "\nNo. of visible edges: " + analysis.getFilteredGraph().getEdges().size()
                    + "\nAverage Degree: " + String.format("%.2f", analysis.getMeanDegree());
        }

        infoTextArea.setText(infoText);
    }

    /**
     * Displays an abundance plot of the selected taxa
     */
    @FXML
    private void displayAbundancePlot() {
        ObservableList selectedItems = LoadedData.getGraphView().getSelectionModel().getSelectedItems();
        List<TaxonNode> nodesList = new LinkedList<>();
        for (Object selectedItem : selectedItems) {
            nodesList.add(((MyVertex) selectedItem).getTaxonNode());
        }
        HashMap<Sample, HashMap<TaxonNode, Integer>> abundancesMap = SampleComparison.calcAbundances(nodesList);

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Taxa");
        yAxis.setLabel("Abundance");
        BarChart<String, Number> abundancePlot = new BarChart<>(xAxis, yAxis);

        for (Map.Entry<Sample, HashMap<TaxonNode, Integer>> entry : abundancesMap.entrySet()) {
            XYChart.Series<String, Number> sampleSeries = new XYChart.Series<>();
            sampleSeries.setName(entry.getKey().getName());
            for (Map.Entry<TaxonNode, Integer> innerMapEntry : entry.getValue().entrySet()) {
                sampleSeries.getData().add(new XYChart.Data<>(innerMapEntry.getKey().getName(), innerMapEntry.getValue()));
            }
            abundancePlot.getData().add(sampleSeries);
        }


        //Display chart on a new pane
        Stage chartStage = new Stage();
        chartStage.setTitle("Abundance Plot");
        Scene chartScene = new Scene(abundancePlot);
        chartStage.setScene(chartScene);
        chartStage.show();
    }

    /**
     * shows the graph in the main view
     *
     * @param taxonGraph
     */
    private void displayGraph(MyGraph<MyVertex, MyEdge> taxonGraph) {
        MyGraphView graphView = new MyGraphView(taxonGraph);
        LoadedData.setGraphView(graphView);
        ViewPane viewPane = new ViewPane(graphView);
        viewPane = new ViewPane(graphView);

        // Bind node hover status text
        statusRightLabel.textProperty().bind(viewPane.hoverInfo);

        // Settings need to be initialized with graphView
        bindGraphSettings(graphView);
        bindColourSettings(graphView);

        mainViewTab.setContent(viewPane);

        //Bind the showLabels-Checkbox to the visibility properties of the MyVertexView labels
        for (Node node : LoadedData.getGraphView().getMyVertexViewGroup().getChildren()) {
            MyVertexView vertexView = (MyVertexView) node;
            vertexView.getVertexLabel().visibleProperty().bind(showLabelsCheckBox.selectedProperty());
        }

        //call displayInfoText whenever the selection changes + decide whether or not to enable the abundancePlotButton
        LoadedData.getGraphView().getSelectionModel().getSelectedItems().addListener((InvalidationListener) e -> {
            displayInfoText();
            if (LoadedData.getGraphView().getSelectionModel().getSelectedItems().size() == 0)
                abundancePlotButton.setDisable(true);
            else
                abundancePlotButton.setDisable(false);
        });


    }

    /**
     * shows the correlation table in the analysis view
     */
    @FXML
    private void displayCorrelationTable() {
        //Delete whatever's been in the table before
        TableView<String[]> analysisTable = new TableView<>();

        //We want to display correlations and p-Values of every node combination
        double[][] correlationMatrix = AnalysisData.getCorrelationMatrix().getData();
        double[][] pValueMatrix = AnalysisData.getPValueMatrix().getData();
        LinkedList<TaxonNode> taxonList = SampleComparison.getUnifiedTaxonList(
                LoadedData.getSamplesToAnalyze(), AnalysisData.getLevelOfAnalysis());


        //Table will consist of strings
        String[][] tableValues = new String[correlationMatrix.length][correlationMatrix[0].length + 1];

        //Add the values as formatted strings
        for (int i = 0; i < tableValues.length; i++) {
            tableValues[i][0] = taxonList.get(i).getName();
            for (int j = 1; j < tableValues[0].length; j++) {
                tableValues[i][j] = String.format("%.3f", correlationMatrix[i][j - 1]).replace(",", ".")
                        + "\n(" + String.format("%.2f", pValueMatrix[i][j - 1]).replace(",", ".") + ")";
            }
        }

        for (int i = 0; i < tableValues[0].length; i++) {
            String columnTitle;
            if (i > 0) {
                columnTitle = taxonList.get(i - 1).getName();
            } else {
                columnTitle = "";
            }
            TableColumn<String[], String> column = new TableColumn<>(columnTitle);
            final int columnIndex = i;
            column.setCellValueFactory(cellData -> {
                String[] row = cellData.getValue();
                return new SimpleStringProperty(row[columnIndex]);
            });
            analysisTable.getColumns().add(column);

            //First column contains taxon names and should be italic
            if (i == 0)
                column.setStyle("-fx-font-style:italic;");
        }

        for (int i = 0; i < tableValues.length; i++) {
            analysisTable.getItems().add(tableValues[i]);
        }

        //Display table on a new stage
        Stage tableStage = new Stage();
        tableStage.setTitle("Correlation Table");
        BorderPane tablePane = new BorderPane();
        Button exportCorrelationsButton = new Button("Save correlation table to CSV");
        Button exportPValuesButton = new Button("Save p-value table to CSV");
        exportCorrelationsButton.setOnAction(e -> exportTableToCSV(tableValues, false));
        exportPValuesButton.setOnAction(e -> exportTableToCSV(tableValues, true));
        HBox exportBox = new HBox(exportCorrelationsButton, exportPValuesButton);
        exportBox.setPadding(new Insets(10));
        exportCorrelationsButton.setPadding(new Insets(0,10,0,0));
        tablePane.setTop(exportBox);
        tablePane.setCenter(analysisTable);
        Scene tableScene = new Scene(tablePane);
        tableStage.setScene(tableScene);
        tableStage.show();
    }

    /**
     * exports the created table to a .csv file
     * uses a fileChooser to determine where to save the .csv file
     *
     * @param tableValues
     * @param isPValue
     */
    private void exportTableToCSV(String[][] tableValues, boolean isPValue) {
        //We'll split up the table values into two parts - if we need correlations, we take the 0th one,
        // if we want p values, we take the 1st
        int splitNumber;
        splitNumber = isPValue ? 1 : 0;

        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File((String) UserSettings.userSettings.get("defaultFileChooserLocation")));
        File outputFile = chooser.showSaveDialog(getPrimaryStage());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            //Write names into first row
            writer.write(",");
            for (int i = 0; i < tableValues.length; i++) {
                writer.write(tableValues[i][0] + ",");
            }
            writer.write("\n");

            for (String[] tableRow : tableValues) {
                for (String s : tableRow) {
                    String[] split = s.split("\n");

//                    System.out.println(s + split.length);
//                    for (String s1 : split) {
//                        System.out.println(s1);
//                    }
                    if (split.length > 1)
                        writer.write(split[splitNumber] + ",");
                    else
                        writer.write(split[0] + ",");
                }
                writer.write("\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates the data for the analysis pane and displays it
     * data created includes:
     * highest positive/negative correlations
     * average counts
     */
    private void displayAnalysisTextsAndGraphs() {
        //Display node with highest frequency
        double highestFrequency = AnalysisData.getHighestFrequency();
        TaxonNode nodeWithHighestFrequency = AnalysisData.getNodeWithHighestFrequency();
        dataStatText.setText("Highest Frequency:\n"
                + nodeWithHighestFrequency.getName() + " (" + String.format("%.3f", highestFrequency) + ")\n");

        //Display nodes with highest positive/negative correlation
        RealMatrix correlationMatrix = AnalysisData.getCorrelationMatrix();
        int[] highestPositiveCorrelationCoordinates = AnalysisData.getHighestPositiveCorrelationCoordinates();
        int[] highestNegativeCorrelationCoordinates = AnalysisData.getHighestNegativeCorrelationCoordinates();
        LinkedList<TaxonNode> taxonList = SampleComparison.getUnifiedTaxonList(LoadedData.getSamplesToAnalyze(), AnalysisData.getLevelOfAnalysis());
        TaxonNode hPCNode1 = taxonList.get(highestPositiveCorrelationCoordinates[0]);
        TaxonNode hPCNode2 = taxonList.get(highestPositiveCorrelationCoordinates[1]);
        TaxonNode hNCNode1 = taxonList.get(highestNegativeCorrelationCoordinates[0]);
        TaxonNode hNCNode2 = taxonList.get(highestNegativeCorrelationCoordinates[1]);

        dataStatText.setText(dataStatText.getText() + "\nHighest Positive Correlation:\n"
                + hPCNode1.getName() + " - " + hPCNode2.getName()
                + " (" + String.format("%.3f", correlationMatrix.getEntry(highestPositiveCorrelationCoordinates[0], highestPositiveCorrelationCoordinates[1]))
                + ")\n");
        dataStatText.setText(dataStatText.getText() + "\nHighest Negative Correlation:\n"
                + hNCNode1.getName() + " - " + hNCNode2.getName()
                + " (" + String.format("%.3f", correlationMatrix.getEntry(highestNegativeCorrelationCoordinates[0], highestNegativeCorrelationCoordinates[1]))
                + ")");

        //Generate Data for the pie chart
        frequencyChart.getData().clear();
        HashMap<TaxonNode, Double> averageCounts = SampleComparison.calcAverageCounts(LoadedData.getSamplesToAnalyze(), AnalysisData.getLevelOfAnalysis());
        for (TaxonNode taxonNode : averageCounts.keySet()) {
            PieChart.Data data = new PieChart.Data(taxonNode.getName(), averageCounts.get(taxonNode));
            frequencyChart.getData().add(data);
        }

        analysisPane.setVisible(true);
    }

    /**
     * starts the Graph analysis
     */
    public void performGraphAnalysis() {
        AnalysisData.setAnalysis(new GraphAnalysis(LoadedData.getTaxonGraph()));
    }

    /**
     * collects the data for the barChart
     * data includes:
     * degree distribution
     * hubs
     */
    public void displayGraphAnalysis() {
        //Generate Data for the BarChart
        GraphAnalysis analysis = AnalysisData.getAnalysis();
        HashMap<Integer, Double> degreeDistribution = analysis.getDegreeDistribution();
        XYChart.Series<String, Double> degreeSeries = new XYChart.Series<>();

        for (Map.Entry<Integer, Double> entry : degreeDistribution.entrySet()) {
            degreeSeries.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        degreeDistributionChart.getData().clear();
        degreeDistributionChart.getData().add(degreeSeries);

        //Generate Graph Statistics to display in the TextArea
        HashMap<TaxonNode, Integer> hubs = analysis.getHubsList();
        graphStatText.setText("List of Hubs:\n\n");

        //Sort hubs by descending values
        Map<TaxonNode, Integer> hubsSorted = hubs.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        for (Map.Entry<TaxonNode, Integer> entry : hubsSorted.entrySet()) {
            graphStatText.setText(graphStatText.getText() + entry.getKey().getName() + " (" + entry.getValue() + ")\n");
        }
    }

    /**
     * displays the modularity
     */
    public void displayMaximalModularity() {
        GraphAnalysis analysis = AnalysisData.getAnalysis();
        double maxModularity = analysis.findGlobalMaximumModularity();
        modularityText.setText("Maximal modularity for current graph:\n" + String.format("%.3f", maxModularity));
    }


    @FXML
    /**
     * opens a file chooser and gives the user the possibility to select a file
     * file chooser default location is where save states are
     */ public void openRecentFile() {
        /*openFileWindow();*/
    }

    @FXML
    /**<h1>Closes the current project and empties the tree view</h1>
     * Empties every data structure that holds something sample related.
     */ public void closeProject() {
        if (treeViewFiles.getRoot() != null) {
            LoadedData.closeProject(treeViewFiles);
            if (mainViewTab.getContent() != null) {
                mainViewTab.setContent(null);
            }
            if (LoadedData.getGraphView() != null && LoadedData.getGraphView().animationService.isRunning()) {
                LoadedData.getGraphView().animationService.cancel();
            }
            collapseAllButton.setDisable(true);
        }
        analysisPane.setVisible(false);
        rankChoiceBox.setValue(null);
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
    public void openBiomV1Files() {
        openFiles(FileType.biomV1);
    }

    @FXML
    public void openBiomV2Files() {
        openFiles(FileType.biomV1);
    }

    @FXML
    public void openQiimeFiles() {
        openFiles(FileType.qiime);
    }

    /**
     * Exits the program
     * quitButton
     */
    @FXML
    public void quit() {
        confirmQuit();
    }

    /**
     * sets the panes width to the passed value
     *
     * @param width
     */
    private void setPanesWidth(int width) {
        leftPane.setMaxWidth(width);
        rightPane.setMaxWidth(width);
        leftPane.setMinWidth(width);
        rightPane.setMinWidth(width);
    }

    /**
     * <h1>Lets the user choose a file / files to load.</h1>
     * Distinguishes which filetype is about to be loaded by the user
     * and calls the associated methods.
     * @param fileType Enum to differentiate which type of file is loaded by the user.
     */
    private void openFiles(FileType fileType) {
        FileChooser fileChooser = new FileChooser();
        String fileChooserTitle = "Load from ";

        if ((Boolean) UserSettings.userSettings.get("isDefaultFileChooserLocation")) {
            setDefaultOpenDirectory(fileChooser);
        } else {
            fileChooser.setInitialDirectory(new File((String) UserSettings.userSettings.get("defaultFileChooserLocation")));
        }

        switch (fileType) {
            case taxonId2Count:
                fileChooser.setTitle(fileChooserTitle + "taxonId2Count file");
                break;
            case readName2TaxonId:
                fileChooser.setTitle(fileChooserTitle + "readName2TaxonId file");
                break;
            case biomV1:
                fileChooser.setTitle(fileChooserTitle + "biomV1 file");
                break;
            case biomV2:
                fileChooser.setTitle(fileChooserTitle + "biomV2 file");
                break;
            case qiime:
                fileChooser.setTitle(fileChooserTitle + "qiime file");
                break;
        }

        //Choose the file / files

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(getPrimaryStage());

        if (selectedFiles != null) {
            ArrayList<String> namesOfAlreadyLoadedFiles = new ArrayList<>();
            for (File file : selectedFiles) {
                String foundFilePath = file.getAbsolutePath();
                if (LoadedData.getOpenFiles() != null && LoadedData.getOpenFiles().contains(foundFilePath)) {
                    namesOfAlreadyLoadedFiles.add(file.getName());
                } else {
                    switch (fileType) {
                        case taxonId2Count:
                            addId2CountFileToTreeView(file);
                            break;
                        case readName2TaxonId:
                            addReadName2TaxonIdFileToTreeView(file);
                            break;
                        case biomV1:
                            addBiomV1FileToTreeView(file);
                            break;
                        case biomV2:
                            addBiomV2FileToTreeView(file);
                            break;
                        case qiime:
                            //TODO HANDLE METADATA PROVIDED BY QIIME
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
            userDirectoryString = "c:/";
        }
        fileChooser.setInitialDirectory(userDirectory);
        UserSettings.userSettings.put("defaultFileChooserLocation", userDirectoryString);
    }

    /**
     * <h1>Parses a given readName2TaxId file</h1>
     * Passes the parsed samples to the LoadedData class.
     * @param file The file the user choses to load
     */
    private void addReadName2TaxonIdFileToTreeView(File file) {
        ReadName2TaxIdCSVParser readName2TaxIdCSVParser = new ReadName2TaxIdCSVParser(TreePreloadService.taxonTree);

        ArrayList<Sample> samples;

        try {
            samples = readName2TaxIdCSVParser.parse(file.getAbsolutePath());
        } catch (IOException e) {
            showWrongFileAlert();
            return;
        } catch (NumberFormatException e) {
            showWrongFileAlert();
            return;
        }

        LoadedData.addSamplesToDatabase(samples, treeViewFiles, file);
        activateButtons();
    }

    /**
     * <h1>Parses a given biomV1 file</h1>
     * Passes the parsed samples to the LoadedData class.
     * @param file The file the user choses to load
     */
    private void addBiomV1FileToTreeView(File file) {
        BiomV1Parser biomV1Parser = new BiomV1Parser(TreePreloadService.taxonTree);

        ArrayList<Sample> samples;

        samples = biomV1Parser.parse(file.getAbsolutePath());

        LoadedData.addSamplesToDatabase(samples, treeViewFiles, file);
        activateButtons();
    }

    /**
     * <h1>Parses a given biomV2 file</h1>
     * Passes the parsed samples to the LoadedData class.
     * @param file The file the user choses to load
     */
    private void addBiomV2FileToTreeView(File file) {
        BiomV2Parser biomV2Parser = new BiomV2Parser(TreePreloadService.taxonTree);

        ArrayList<Sample> samples;

        try {
            samples = biomV2Parser.parse(file.getAbsolutePath());
        } catch (IOException e) {
            showWrongFileAlert();
            return;
        } catch (NumberFormatException e) {
            showWrongFileAlert();
            return;
        }

        LoadedData.addSamplesToDatabase(samples, treeViewFiles, file);
        activateButtons();
    }

    /**
     * <h1>Parses a given id2Count file</h1>
     * Passes the parsed samples to the LoadedData class.
     * @param file The file the user choses to load
     */
    private void addId2CountFileToTreeView(File file) {
        TaxonId2CountCSVParser taxonId2CountCSVParser = new TaxonId2CountCSVParser(TreePreloadService.taxonTree);

        ArrayList<Sample> samples;

        try {
            samples = taxonId2CountCSVParser.parse(file.getAbsolutePath());
        } catch (IOException e) {
            showWrongFileAlert();
            return;
        } catch (NumberFormatException e) {
            showWrongFileAlert();
            return;
        }

        LoadedData.addSamplesToDatabase(samples, treeViewFiles, file);
        activateButtons();
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
     * helper method for setting up the Buttons on the left pane
     */
    public void initializeButtonsOnLeftPane() {
        collapseAllButton.setDisable(true);
        collapseAllButton.setTooltip(new Tooltip("collapse all"));
    }

    @FXML
    /**
     * <h1>Collapses all nodes in the treeview element</h1>
     * Does not effect the current extended state of the treeitems
     */
    public void collapseAll() {
        if (treeViewFiles.getRoot() == null || treeViewFiles.getRoot().getChildren().isEmpty()) {
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
    /**
     * Deselects all nodes in the treeview element
     */
    public void deselectAll() {
        changeSelectionForAll(true);
        System.out.println("Deselection toggled");
    }

    @FXML
    /**
     * Selects all nodes in the treeview element
     */
    public void selectAll() {
        changeSelectionForAll(false);
        System.out.println("Selection toggled");
    }

    /**
     * Deselects all nodes in the treeview element
     */
    private void changeSelectionForAll(boolean isDeselectAll) {
        if (treeViewFiles.getRoot() != null && !treeViewFiles.getRoot().getChildren().isEmpty()) {
            treeViewFiles.getRoot().getChildren()
                    .stream()
                    .map(treeItem -> {
                        CheckBoxTreeItem<String> checkBoxTreeItem = new CheckBoxTreeItem<>();
                        checkBoxTreeItem.setValue(treeItem.getValue());
                        checkBoxTreeItem.getChildren().addAll(treeItem.getChildren());
                        checkBoxTreeItem.setSelected(isDeselectAll ? true : false);
                        return checkBoxTreeItem;
                    })
                    .forEach(treeItem -> System.out.println(treeItem.getValue()));
        }
    }

    /**
     * Starts the tree preload service
     */
    private void startTreePreloadService() {
        TreePreloadService treePreloadService = new TreePreloadService();
        treePreloadService.setOnSucceeded(e -> mainViewTab.setContent(null));
        startupLabel.textProperty().bind(treePreloadService.messageProperty());
        treePreloadService.start();
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
    private void activateButtons() {
        rankChoiceBox.setDisable(false);
        collapseAllButton.setDisable(false);
        LoadedData.getSelectedSamples().addListener((InvalidationListener) e -> displayInfoText());
    }

    /**
     * Initializes the rank selection toggle group and adds a listener to the rank selection
     */
    private void initializeRankChoiceBox() {
        rankChoiceBox.setDisable(true);
        rankChoiceBox.setItems(ranksList);

    }

    /**
     * Hides all the components of the analysis pane, since they should only be displayed when data is loaded
     */
    private void initializeAnalysisPane() {
        analysisPane.setVisible(false);

    }

    /**
     * Sets up listeners that update graph everytime the graph changes
     */
    private void initializeGraphAnalysis() {
        degreeDistributionChart.getXAxis().setLabel("Degree");
        degreeDistributionChart.getYAxis().setLabel("Node Fraction");

        posCorrelationLowerFilterProperty().addListener(observable -> updateView());
        posCorrelationUpperFilterProperty().addListener(observable -> updateView());
        negCorrelationLowerFilterProperty().addListener(observable -> updateView());
        negCorrelationUpperFilterProperty().addListener(observable -> updateView());
        maxPValueProperty().addListener(observable -> updateView());
        minFrequencyProperty().addListener(observable -> updateView());
        maxFrequencyProperty().addListener(observable -> updateView());

    }

    /**
     * updates the view of the whole analysis pane
     */
    private void updateView() {
        if (LoadedData.getTaxonGraph() == null) {
            return;
        }

        LoadedData.getTaxonGraph().filterEdges();
        LoadedData.getTaxonGraph().filterVertices();
        performGraphAnalysis();
        displayGraphAnalysis();
        displayInfoText();
        setHubsInView();
    }

    /**
     * creates the view of the calculated hubs
     */
    private void setHubsInView() {
        HashMap<TaxonNode, Integer> hubsList = AnalysisData.getAnalysis().getHubsList();
        HashMap<TaxonNode, MyVertex> taxonNodeToVertexMap = LoadedData.getTaxonGraph().getTaxonNodeToVertexMap();
        for (Map.Entry<TaxonNode, MyVertex> entry : taxonNodeToVertexMap.entrySet()) {
            if (hubsList.containsKey(entry.getKey()))
                entry.getValue().setIsHub(true);
            else
                entry.getValue().setIsHub(false);
        }
    }

    /**
     * Sets up listeners to call displayInfoText() whenever it is necessary
     */
    private void initializeInfoPane() {
        rankChoiceBox.valueProperty().addListener(e -> displayInfoText());
        compareSelectedSamplesButton.selectedProperty().addListener(e -> displayInfoText());
        abundancePlotButton.setDisable(true);

    }

    /**
     * initializes the bindings of the sliders and the analysis pane
     */
    private void initializeBindings() {
        //First, bind the LoadedData.analyzeAll boolean property to the radio buttons
        LoadedData.analyzeSelectedProperty().bind(compareSelectedSamplesButton.selectedProperty());

        //Since the slider value property is double and the text field property is a string, we need to convert them
        //Defining own class to avoid exceptions
        class MyNumberStringConverter extends NumberStringConverter {
            @Override
            public Number fromString(String value) {
                try {
                    return super.fromString(value);
                } catch (RuntimeException ex) {
                    return 0;
                }
            }
        }
        StringConverter<Number> converter = new MyNumberStringConverter();
        //Bind every slider to its corresponding text field and vice versa
        Bindings.bindBidirectional(minPosCorrelationText.textProperty(), posCorrelationRangeSlider.lowValueProperty(), converter);
        Bindings.bindBidirectional(maxPosCorrelationText.textProperty(), posCorrelationRangeSlider.highValueProperty(), converter);
        Bindings.bindBidirectional(minNegCorrelationText.textProperty(), negCorrelationRangeSlider.lowValueProperty(), converter);
        Bindings.bindBidirectional(maxNegCorrelationText.textProperty(), negCorrelationRangeSlider.highValueProperty(), converter);
        Bindings.bindBidirectional(maxPValueText.textProperty(), maxPValueSlider.valueProperty(), converter);
        Bindings.bindBidirectional(minFrequencyText.textProperty(), frequencyRangeSlider.lowValueProperty(), converter);
        Bindings.bindBidirectional(maxFrequencyText.textProperty(), frequencyRangeSlider.highValueProperty(), converter);
        Bindings.bindBidirectional(excludeFrequencyText.textProperty(), excludeFrequencySlider.valueProperty(), converter);

        //Bind the internal filter properties to the slider values
        AnalysisData.posCorrelationLowerFilterProperty().bind(posCorrelationRangeSlider.lowValueProperty());
        AnalysisData.posCorrelationUpperFilterProperty().bind(posCorrelationRangeSlider.highValueProperty());
        AnalysisData.negCorrelationLowerFilterProperty().bind(negCorrelationRangeSlider.lowValueProperty());
        AnalysisData.negCorrelationUpperFilterProperty().bind(negCorrelationRangeSlider.highValueProperty());
        AnalysisData.minFrequencyProperty().bind(frequencyRangeSlider.lowValueProperty());
        AnalysisData.maxFrequencyProperty().bind(frequencyRangeSlider.highValueProperty());
        AnalysisData.maxPValueProperty().bind(maxPValueSlider.valueProperty());
        AnalysisData.excludeFrequencyThresholdProperty().bind(excludeFrequencySlider.valueProperty());

        //The values of the negative slider can't be set to values below 0 via FXML for reasons beyond human understanding,
        // so we set them manually
        negCorrelationRangeSlider.setLowValue(-1);
        negCorrelationRangeSlider.setHighValue(-0.5);

        //We want the graph to be redone if one of the following occurs:
        //1. Radio button switches between "Analyze All" and "Analyze Selected"
        compareSelectedSamplesButton.selectedProperty().addListener(observable -> {
            if ((!compareSelectedSamplesButton.isSelected() || LoadedData.getSelectedSamples().size() >= 3)
                    && rankChoiceBox.getValue() != null)
                startAnalysis();
        });
        //2. Rank selection changes
        rankChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && LoadedData.getSamplesToAnalyze().size()>=3) {
                AnalysisData.setLevel_of_analysis(newValue.toLowerCase());
                startAnalysis();
            }
        });
        //3. Sample selection changes while "Analyze Selected" is selected AND at least three samples are selected
        LoadedData.getSelectedSamples().addListener((InvalidationListener) observable -> {
            if (compareSelectedSamplesButton.isSelected() && LoadedData.getSelectedSamples().size() >= 3) {
                startAnalysis();
            }
        });
        //4. Correlation radio button is changed
        pearsonCorrelationButton.selectedProperty().addListener(o -> startAnalysis());
        spearmanCorrelationButton.selectedProperty().addListener(o -> startAnalysis());
        kendallCorrelationButton.selectedProperty().addListener(o -> startAnalysis());
        //5. Global frequency threshold is changed
        excludeFrequencySlider.valueProperty().addListener(o -> startAnalysis());
    }

    /**
     * Initialize advanced setting elements to only be visible when checkbox is activated
     */
    public void initializeGraphSettings() {

        labelAnimationSpeed.visibleProperty().bind(checkAdvancedGraphSettings.selectedProperty());
        sliderAnimationSpeed.visibleProperty().bind(checkAdvancedGraphSettings.selectedProperty());
        labelEdgeForce.visibleProperty().bind(checkAdvancedGraphSettings.selectedProperty());
        sliderEdgeForce.visibleProperty().bind(checkAdvancedGraphSettings.selectedProperty());
        labelNodeRepulsion.visibleProperty().bind(checkAdvancedGraphSettings.selectedProperty());
        sliderNodeRepulsion.visibleProperty().bind(checkAdvancedGraphSettings.selectedProperty());
        labelStretchParameter.visibleProperty().bind(checkAdvancedGraphSettings.selectedProperty());
        sliderStretchParameter.visibleProperty().bind(checkAdvancedGraphSettings.selectedProperty());
        buttonResetGraphDefaults.setOnAction(e -> setGraphSettingsDefault());
        setGraphSettingsDefault();
    }

    /**
     * Bind graph setting controls to MyGraphView instance.
     *
     * @param graphView instance to which controls are bound
     */
    public void bindGraphSettings(MyGraphView graphView) {
        // Bind Node Radius Slider to all Nodes in Graph
        for (Node node : graphView.getMyVertexViewGroup().getChildren()) {
            ((MyVertexView) node).getRadiusProperty().bind(sliderNodeRadius.valueProperty());
        }
        // Bind Edge Width Slider to all Edges in Graph
        for (Node node : graphView.getMyEdgeViewGroup().getChildren()) {
            ((MyEdgeView) node).getWidthProperty().bind(sliderEdgeWidth.valueProperty().multiply(Math.abs(((MyEdgeView) node).getMyEdge().getCorrelation()) + 0.1));
        }

        /**buttonPauseAnimation.setOnAction(e -> {
         boolean isRunning = graphView.animationService.isRunning();
         if (isRunning) graphView.animationService.cancel();
         if (!isRunning) graphView.animationService.restart();
         }); **/

        sliderEdgeLength.lowValueProperty().addListener((o, e, n) -> {
            graphView.animationService.setEdgeLengthLow(n.doubleValue());
        });

        sliderEdgeLength.highValueProperty().addListener((o, e, n) -> {
            graphView.animationService.setEdgeLengthHigh(n.doubleValue());
        });

        sliderNodeRepulsion.valueProperty().addListener((o, e, n) -> {
            graphView.animationService.setNodeRepulsion(n.intValue());
        });

        sliderStretchParameter.valueProperty().addListener((o, e, n) -> {
            graphView.animationService.setStretchForce(n.doubleValue());
        });

        sliderEdgeForce.valueProperty().addListener((o, e, n) -> {
            graphView.animationService.setForce(n.doubleValue());
        });


        sliderAnimationSpeed.valueProperty().addListener((o, e, n) -> {
            Double fr = sliderAnimationSpeed.getMax() - n.doubleValue();
            graphView.animationService.setFrameRate(fr.intValue());
        });

        buttonPauseAnimation.selectedProperty().bindBidirectional(graphView.pausedProperty);


    }

    @FXML
    /**
     * resets the filter values to the default values
     */
    public void resetFilterSettings() {
        posCorrelationRangeSlider.setLowValue(DEFAULT_POSITIVE_CORRELATION_LOW);
        posCorrelationRangeSlider.setHighValue(DEFAULT_POSITIVE_CORRELATION_HIGH);
        negCorrelationRangeSlider.setLowValue(DEFAULT_NEGATIVE_CORRELATION_LOW);
        negCorrelationRangeSlider.setHighValue(DEFAULT_NEGATIVE_CORRELATION_HIGH);
        maxPValueSlider.setValue(DEFAULT_MAX_P_VALUE_SLIDER);
        frequencyRangeSlider.setLowValue(DEFAULT_FREQUENCY_RANGE_SLIDER_LOW);
        frequencyRangeSlider.setHighValue(DEFAULT_FREQUENCY_RANGE_SLIDER_HIGH);
    }

    /**
     * Initializes the radio buttons for the colour selection
     */
    private void bindColourSettings(MyGraphView graphView) {
        //Node settings
        colourRadioNodeFix.setOnAction( e -> {
            setPalette(nodeColourCombo, Palette.CATEGORICAL);
            graphView.setNodeAttribute("fix");
            graphView.setNodeColour(nodeColourCombo.getValue());
        });

       colourRadioNodeParent.setOnAction(e -> {
           setPalette(nodeColourCombo, Palette.CATEGORICAL);
           graphView.setNodeAttribute("parentName");
           graphView.setNodeColour(nodeColourCombo.getValue());
       });

       colourRadioNodeAlpha.setOnAction( e -> {
           setPalette(nodeColourCombo, Palette.DIV);
           graphView.setNodeAttribute("alpha");
           graphView.setNodeColour(nodeColourCombo.getValue());

       });

       colourRadioNodeFrequency.setOnAction(e -> {
           setPalette(nodeColourCombo, Palette.SEQ);
           graphView.setNodeAttribute("frequency");
           graphView.setNodeColour(nodeColourCombo.getValue());
       });

        nodeColourCombo.setOnAction( e -> {
            graphView.setNodeColour(nodeColourCombo.getValue());
        });


        // Edge settings
       colourRadioEdgeCorrelation.setOnAction( e -> {
           setPalette(edgeColourCombo, Palette.DIV);
           graphView.setEdgeAttribute("correlation");
           graphView.setEdgeColour(edgeColourCombo.getValue());
       });

       /**colourRadioEdgeDistance.setOnAction( e-> {
           setPalette(edgeColourCombo, Palette.DIV);
           graphView.set
       }); **/

      colourRadioEdgePvalue.setOnAction( e -> {
          setPalette(edgeColourCombo, Palette.SEQ);
          graphView.setEdgeAttribute("pValue");
          graphView.setEdgeColour(edgeColourCombo.getValue());
      });

      edgeColourCombo.setOnAction( e -> {
          graphView.setEdgeColour(edgeColourCombo.getValue());
      });
    }

    /**
     * Helper function to set new Palette Spetrum in ComboBox
     * @param cb ComboBox which should be adapted
     * @param p EnumSet of Palette values to be displayed
     */
    private void setPalette(ComboBox cb, EnumSet p) {
        cb.setItems(FXCollections.observableArrayList(p));
        cb.getSelectionModel().selectFirst();
    }

    /**
     * Initializes custom Combo selection Box for color gradient choosers
     */
    private void initializeColorComboBox() {
        // Node Parameters
        nodeColourCombo.setPrefWidth(200);
        colourNodeComboContainer.getChildren().add(nodeColourCombo);

        // Edge parameters
        edgeColourCombo.setPrefWidth(200);
        colourEdgeComboContainer.getChildren().add(edgeColourCombo);
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
        Hyperlink hyperlink = new Hyperlink();
        hyperlink.setText("https://github.com/jmueller95/CORNETTO");
        Text text = new Text("Cornetto is a modern tool to visualize and calculate correlations between" +
                "samples.\nIt was created in 2017 by students of the group of Professor Huson in Tübingen.\nThe group" +
                "was supervised by Caner Bagci.\n\n" +
                "This project is licensed under the MIT License.\n\n" +
                "For more information go to: ");

        TextFlow textFlow = new TextFlow(text, hyperlink);

        text.setWrappingWidth(500);
        aboutAlert = new Alert(Alert.AlertType.INFORMATION);
        aboutAlert.setTitle("About " + GlobalConstants.NAME_OF_PROGRAM);
        aboutAlert.setHeaderText("What is " + GlobalConstants.NAME_OF_PROGRAM);
        aboutAlert.getDialogPane().setContent(textFlow);
        aboutAlert.show();
    }

    /**
     * Prompts an alert if the user tries to load a file that does not match the requirements.
     */
    //TODO: If multiple files are wrong, not every file should get its own alert.
    private void showWrongFileAlert() {
        wrongFileAlert = new Alert(Alert.AlertType.ERROR);
        wrongFileAlert.setTitle("File not loaded");
        wrongFileAlert.setHeaderText("Invalid file.");
        wrongFileAlert.setContentText("You tried to load a file with a wrong file type.");
        wrongFileAlert.show();
    }

    /**
     * Prompts an alert that the selected file is already part of the current project.
     */
    private void showFileAlreadyLoadedAlert(ArrayList<String> fileNames) {
        if (fileNames.size() > 1) {
            fileNames = fileNames
                    .stream()
                    .map(string -> "'" + string + "'")
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        String name = String.join(",\n", fileNames);

        String oneFileAlreadyLoaded = "The file \n'" + name + "'\nis already loaded in your project.";
        String multipleFilesAlreadyLoaded = "The files\n" + name + "\n are already loaded in your project.";
        fileAlreadyLoadedAlert = new Alert(Alert.AlertType.ERROR);
        fileAlreadyLoadedAlert.setTitle("File not loaded.");
        fileAlreadyLoadedAlert.setContentText(fileNames.size() == 1 ? oneFileAlreadyLoaded : multipleFilesAlreadyLoaded);
        fileAlreadyLoadedAlert.show();
    }

    /**
     * Prompts an alert telling the user that the chosen data is not sufficient for an analysis
     */
    private void showInsufficientDataAlert() {
        insufficientDataAlert = new Alert(Alert.AlertType.ERROR);
        insufficientDataAlert.setTitle("Insufficient data for Analysis");
        insufficientDataAlert.setHeaderText("Not enough data to perform the analysis.");
        insufficientDataAlert.setContentText("Try choosing a more specific rank!");
        insufficientDataAlert.show();
    }

    /**
     * Opens new PopUp Window with Image Export options.
     */
    @FXML
    private void exportImages() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("src/UI/exportImageGUI"));
            loader.setLocation(new URL("file:" + new File("").getCanonicalPath().concat("/src/UI/exportImageGUI.fxml")));
            //Parent root = loader.load();
            ExportImageController exportImageController = new ExportImageController(viewPane);
            //ExportImageController exportImageController = loader.getController();
            //exportImageController.setViewPane(viewPane);
            loader.setController(exportImageController);
            Parent root = loader.load();
            exportImagesStage = new Stage();
            exportImagesStage.setTitle("Export Image");
            Scene exportImageScene = new Scene(root, 300, 200);
            exportImagesStage.setScene(exportImageScene);
            exportImageScene.getStylesheets().add(GlobalConstants.DARKTHEME);
            exportImagesStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Scene optionsScene = new Scene(root, 1000, 700);
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

    /**
     * Sets all slider elements in the graph settings menu to default values
     */
    private void setGraphSettingsDefault() {
        sliderAnimationSpeed.setValue(DEFAULT_ANIMATION_SPEED);
        sliderEdgeForce.setValue(DEFAULT_SLIDER_EDGE_FORCE);
        sliderNodeRepulsion.setValue(DEFAULT_NODE_REPULSION);
        sliderStretchParameter.setValue(DEFAULT_SLIDER_STRECH_PARAMETER);
        sliderNodeRadius.setValue(DEFAULT_SLIDER_NODE_RADIUS);
        sliderEdgeWidth.setValue(DEFAULT_SLIDER_EDGE_WIDTH);
        sliderEdgeLength.setLowValue(DEFAULT_SLIDER_EDGE_LENGTH_LOW);
        sliderEdgeLength.setHighValue(DEFAULT_SLIDER_EDGE_LENGTH_HIGH);
    }

    public static Stage getOptionsStage() {
        return optionsStage;
    }
}
