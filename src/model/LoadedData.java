package model;

import analysis.SampleComparison;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.Callback;
import org.apache.commons.math3.linear.RealMatrix;
import view.MyGraphView;

import java.io.File;
import java.util.*;

/**
 * <h1>Class that stores data parsed from the loaded files</h1>
 * The TreeView Element inside of the MainStageController is now separated
 * from the actual data which is stored in the 'samples' field.
 * Operations on the data that is presented in the 'samples' field of
 * this class is placed here as well.
 * <p>
 * <b>Note:</b> This class is not intended to store methods or data
 * that is connected with analysis. The AnalysisData class has been
 * created for this purpose.
 * </p>
 *
 * @see AnalysisData
 */
public class LoadedData {

    private static ObservableList<Sample> samples;
    private static ArrayList<String> openFiles;
    private static MyGraph<MyVertex, MyEdge> taxonGraph;
    private static MyGraphView graphView;
    private static HashMap<String, Sample> sampleNameToSample = new HashMap<>();
    private static ObservableList<Sample> selectedSamples = FXCollections.observableArrayList();
    private static BooleanProperty analyzeSelected = new SimpleBooleanProperty(false);
    private static int countOfSamplesFromEqualPaths = 0;

    public static void addSamplesToDatabase(ArrayList<Sample> loadedSamples, TreeView<String> treeViewFiles, File file) {
        //Add file path to every sample. (Multiple samples from the same file do have the same file path)
        loadedSamples
                .stream()
                .forEach(sample -> sample.setPathToFile(file.getAbsolutePath()));

        if (samples == null) {
            samples = FXCollections.observableArrayList(loadedSamples);
        } else {
            samples.addAll(loadedSamples);
        }
        if (openFiles == null) {
            openFiles = new ArrayList<>();
        }
        openFiles.add(file.getAbsolutePath());
        addSamplesToTreeView(treeViewFiles, loadedSamples, file.getName());
    }

    /**
     * Builds a fully connected graph of all taxa contained in the sample list.
     * This method must be called AFTER analysis is performed, since it needs the correlations and p-values
     */
    public static void createGraph() {
        LinkedList<TaxonNode> nodeList = SampleComparison.getUnifiedTaxonList(samples, AnalysisData.getLevelOfAnalysis());
        taxonGraph = new MyGraph<>();

        //Create a vertex for each taxonNode
        for (TaxonNode taxonNode : nodeList) {
            MyVertex vertex = new MyVertex(taxonNode, nodeList.size() - 1); //It will be a fully connected graph
            taxonGraph.addVertex(vertex);
            //Add mapping of node to vertex to hashmap
            taxonGraph.getTaxonNodeToVertexMap().put(taxonNode, vertex);
        }

        //Connect every pair of vertices with a MyEdge, set correlation and pValue of the edge
        final HashMap<TaxonNode, MyVertex> taxonNodeToVertexMap = taxonGraph.getTaxonNodeToVertexMap();
        final RealMatrix correlationMatrix = AnalysisData.getCorrelationMatrix();
        final RealMatrix pValueMatrix = AnalysisData.getPValueMatrix();
        final double[][] mdsMatrix = AnalysisData.getMDSMatrix();

        for (int i = 0; i < nodeList.size(); i++) {
            //Create Hashmap for this index, or access it if it's already there
            HashMap<Integer, MyEdge> currentEdgeMap =
                    taxonGraph.getNodeIdsToEdgesMap().getOrDefault(nodeList.get(i).getTaxonId(), new HashMap<>());

            // Set inital coordinates from MDS
            MyVertex sourceVertex = taxonNodeToVertexMap.get(nodeList.get(i));
            //sourceVertex.xCoordinatesProperty().setValue(mdsMatrix[i][0]*10);
            //sourceVertex.yCoordinatesProperty().setValue(mdsMatrix[i][1]*10);


            for (int j = 0; j < i; j++) {

                MyVertex targetVertex = taxonNodeToVertexMap.get(nodeList.get(j));
                MyEdge edge = new MyEdge(sourceVertex, targetVertex);
                edge.setCorrelation(correlationMatrix.getEntry(i, j));
                edge.setPValue(pValueMatrix.getEntry(i, j));
                taxonGraph.addEdge(edge, sourceVertex, targetVertex);
                currentEdgeMap.put(nodeList.get(j).getTaxonId(), edge);
                //Get j's hashmap or create it
                HashMap<Integer, MyEdge> secondNodeEdgeMap =
                        taxonGraph.getNodeIdsToEdgesMap().getOrDefault(nodeList.get(j).getTaxonId(), new HashMap<>());
                //Add edge in other direction
                secondNodeEdgeMap.put(nodeList.get(i).getTaxonId(), edge);
                //Add j's hashmap, if it's not contained yet
                taxonGraph.getNodeIdsToEdgesMap().putIfAbsent(nodeList.get(j).getTaxonId(), secondNodeEdgeMap);
            }
            //Add Hashmap to map of maps, if it's not contained yet
            taxonGraph.getNodeIdsToEdgesMap().putIfAbsent(nodeList.get(i).getTaxonId(), currentEdgeMap);
        }

    }


    /**
     * <h1>Closes all open files and empties associated data structures</h1>
     * Empties the openFiles ArrayList and updates the tree view fxml element.
     * Empties openFiles and samples.
     * <p>
     * <b>Note: </b> Project must be saved / alert for saving must be prompted beforehand.
     * <b>Every progress / filtering is lost after this point!</b>
     *
     * @param treeViewFiles Tree view fxml element to display the loaded samples
     */
    public static void closeProject(TreeView<String> treeViewFiles) {
        samples.clear();
        if (!treeViewFiles.getRoot().getChildren().isEmpty()) {
            treeViewFiles.getRoot().getChildren().remove(0, treeViewFiles.getRoot().getChildren().size());
            LoadedData.getOpenFiles().clear();
            samples.clear();
            selectedSamples.clear();
            //TODO:: Kill graph view when Project is closed
        }
    }

    /**
     * <h1>Adds samples to the visible samples / taxa listing</h1>
     * Initializes the treeview in case it is null
     * <p>
     * <p>Note:</p> In case of loading a file that inherits samples that have been
     * deleted before it completely removes those samples and replaces them with the
     * ones in the given file.
     * @param treeViewFiles Tree view fxml element to display the loaded samples
     */
    private static void addSamplesToTreeView(TreeView<String> treeViewFiles, ArrayList<Sample> loadedSamples, String fileName) {
        //If no samples have been loaded so far
        if (treeViewFiles.getRoot() == null) {
            generateTreeViewStructure(treeViewFiles);
        }
        TreeItem<String> newRoot, newRootID, newRootCount;

        boolean isLookedForLoadedSamples = false;

        countOfSamplesFromEqualPaths = 0;
        for (Sample sample : loadedSamples) {
            if (!isLookedForLoadedSamples && treeViewFiles.getRoot().getChildren() != null && !treeViewFiles.getRoot().getChildren().isEmpty()) {

                String pathToNewSample = sample.getPathToFile();

                /*String pathToSample = sample.getPathToFile();

                for (int i = 0; i < treeViewFiles.getRoot().getChildren().size(); i++) {
                    Sample alreadyLoadedSample = samples.get(i);
                    String pathToFileOfAlreadyLoadedSample = alreadyLoadedSample.getPathToFile();
                    if (pathToFileOfAlreadyLoadedSample.equals(pathToSample)) {
                        String pathToFileOfAlreadyLoadedSampleWithoutExtension = getNameWithoutExtension(pathToFileOfAlreadyLoadedSample, new ArrayList<>());
                        samples.remove(alreadyLoadedSample);
                        sampleNameToSample.remove(pathToFileOfAlreadyLoadedSampleWithoutExtension);
                        treeViewFiles.getRoot().getChildren().remove(i, ++i);
                    }
                }

                isLookedForLoadedSamples = true;*/
            }
            String sampleName = getNameWithoutExtension(fileName, loadedSamples);
            sample.setName(sampleName);
            CheckBoxTreeItem<String> newSample = new CheckBoxTreeItem<>(sampleName);
            newSample.selectedProperty().addListener((observable, oldValue, newValue) -> selectOrDeselectSample(newValue, oldValue, newSample));
            MenuItem addMenuItem = new MenuItem("delete");

            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                newRoot = new TreeItem<>(taxonNode.getName());
                newSample.getChildren().add(newRoot);

                newRootID = new TreeItem<>("id: " + taxonNode.getTaxonId());
                newRootCount = new TreeItem<>("count: " + sample.getTaxonCountRecursive(taxonNode));
                newRoot.getChildren().addAll(newRootID, newRootCount);
            }
            treeViewFiles.getRoot().getChildren().add(newSample);
            sampleNameToSample.put(sampleName, sample);
        }
    }

    /**
     * <h1>Initializes the TreeView FXML element.</h1>
     * The TreeView is divided into CheckBoxTreeItems as representation of samples
     * and TreeItems as representation of Taxa and information concerning those Taxa
     * @param treeViewFiles
     */
    private static void generateTreeViewStructure(TreeView<String> treeViewFiles) {
        treeViewFiles.setRoot(new TreeItem<>("root"));
        //The classic treeview only has one root item, but you can work around this by just setting it to invisible
        treeViewFiles.setShowRoot(false);
        treeViewFiles.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> param) {
                return new CheckBoxTreeCell<String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        //If there is no information for the Cell, make it empty
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                            //Otherwise if it's not representation as an item of the tree
                            //is not a CheckBoxTreeItem, remove the checkbox item
                        } else if (!(getTreeItem() instanceof CheckBoxTreeItem)) {
                            setGraphic(null);
                        } else if (getTreeItem() instanceof CheckBoxTreeItem) {
                            MenuItem removeSample = new MenuItem("remove");
                            removeSample.setOnAction(event -> {
                                int indexOfTreeItem = treeViewFiles.getRoot().getChildren().indexOf(getTreeItem());
                                removeSampleFromDatabase(getTreeItem().getValue(), treeViewFiles, indexOfTreeItem);
                            });
                            setContextMenu(new ContextMenu(removeSample));
                        }
                    }
                };
            }
        });
    }

    /**
     * <h1>Decides based on the given values whether a CheckBoxTreeItem has been selected or deselected.</h1>
     * @param newValue Boolean -> Is CheckBox selected?
     * @param oldValue Boolean -> Is CheckBox selected?
     * @param newSample CheckBoxTreeItem associated with the above values.
     */
    private static void selectOrDeselectSample(boolean newValue, boolean oldValue, CheckBoxTreeItem<String> newSample) {
        if (newValue && !oldValue) {
            selectedSamples.add(sampleNameToSample.get(newSample.getValue()));
        } else {
            selectedSamples.remove(sampleNameToSample.get(newSample.getValue()));
        }
    }

    /**
     * <h1>Completely erases any information stored about a sample</h1>
     * @param sampleName Name of the sample that is about to be removed
     * @param treeViewFiles TreeView FXML element that is placed on the left pane
     * @param indexOfTreeItem Index of the sample that is about to be removed inside the TreeView
     */
    private static void removeSampleFromDatabase(String sampleName, TreeView<String> treeViewFiles, int indexOfTreeItem) {
        String pathToFileOfDeletedSample = getSampleNameToSample().get(sampleName).getPathToFile();
        samples.remove(sampleNameToSample.get(sampleName));
        String totalPathToFile = getSampleNameToSample().get(sampleName).getPathToFile();
        boolean isOnlyLeft =
                samples
                .stream()
                .noneMatch(sample -> sample.getPathToFile().equals(totalPathToFile));
        if (isOnlyLeft) {
            openFiles.remove(getSampleNameToSample().get(sampleName).getPathToFile());
        }
        sampleNameToSample.remove(sampleName);
        treeViewFiles.getRoot().getChildren().remove(indexOfTreeItem, ++indexOfTreeItem);
    }

    /**
     * <h1>Removes the file extension from the file name.</h1>
     * <p>
     * <p>Note:</p> In case of multiple samples in one file a number is placed in front of the samples to ease the differentiation
     * between them.
     * @param fileName Name of the file.
     * @param loadedSamples The samples that have been loaded recently. (Only > 1 in case of multiple samples in one file)
     * @return Name of file without file extension.
     */
    private static String getNameWithoutExtension(String fileName, ArrayList<Sample> loadedSamples) {
        String[] fileNameSplit = fileName.split("\\.");
        String fileNameWithoutExtension = (String.join(".", Arrays.copyOfRange(fileNameSplit, 0, fileNameSplit.length - 1)));
        return (loadedSamples.size() > 1 ? "[" + ++countOfSamplesFromEqualPaths + "] " + fileNameWithoutExtension : fileNameWithoutExtension);
    }

    public static ObservableList<Sample> getSamplesToAnalyze() {
        return analyzeSelected.get() ? selectedSamples : samples;
    }

    // GETTERS
    public static ObservableList<Sample> getSamples() {
        return samples;
    }

    public static MyGraph<MyVertex, MyEdge> getTaxonGraph() {
        return taxonGraph;
    }

    public static HashMap<String, Sample> getSampleNameToSample() {
        return sampleNameToSample;
    }

    public static ObservableList<Sample> getSelectedSamples() {
        return selectedSamples;
    }

    public static boolean isAnalyzeSelected() {
        return analyzeSelected.get();
    }

    public static BooleanProperty analyzeSelectedProperty() {
        return analyzeSelected;
    }

    public static MyGraphView getGraphView() {
        return graphView;
    }

    public static ArrayList<String> getOpenFiles() {
        return openFiles;
    }

    // SETTERS
    public static void setSamples(ObservableList<Sample> samples) {
        LoadedData.samples = samples;
    }

    public static void setAnalyzeSelected(boolean analyzeSelected) {
        LoadedData.analyzeSelected.set(analyzeSelected);
    }

    public static void setGraphView(MyGraphView graphView) {
        LoadedData.graphView = graphView;
    }
}
