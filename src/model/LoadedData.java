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
     * Empties the openFiles ArrayList and updates the tree view fxml element
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
     * <h1>Initializes the visible samples / taxa listing</h1>
     * Overwrites the treeView fxml element in the mainStageController with all elements of the samples ArrayList
     *
     * @param treeViewFiles Tree view fxml element to display the loaded samples
     */
    private static void addSamplesToTreeView(TreeView<String> treeViewFiles, ArrayList<Sample> loadedSamples, String fileName) {
        //If no samples have been loaded so far
        if (treeViewFiles.getRoot() == null) {
            generateTreeViewStructure(treeViewFiles);
        }
        TreeItem<String> newRoot, newRootID, newRootCount;

        countOfSamplesFromEqualPaths = 0;
        for (Sample sample : loadedSamples) {
            if (treeViewFiles.getRoot().getChildren() != null && !treeViewFiles.getRoot().getChildren().isEmpty()) {

                /*String pathToSample = sample.getPathToFile();
                samples
                        .stream()
                        .map(foundSample -> foundSample.getPathToFile())
                        .forEach(pathToFoundSample -> {
                            if (pathToSample.equals(pathToFoundSample)) {
                                samples.remove(sample);
                                System.out.println("Removed " + pathToSample);
                            }
                        });*/

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

    private static void selectOrDeselectSample(boolean newValue, boolean oldValue, CheckBoxTreeItem<String> newSample) {
        if (newValue && !oldValue) {
            selectedSamples.add(sampleNameToSample.get(newSample.getValue()));
        } else {
            selectedSamples.remove(sampleNameToSample.get(newSample.getValue()));
        }
    }

    private static void removeSampleFromDatabase(String sampleName, TreeView<String> treeViewFiles, int indexOfTreeItem) {
        String pathToFileOfDeletedSample = getSampleNameToSample().get(sampleName).getPathToFile();
        openFiles.remove(getSampleNameToSample().get(sampleName).getPathToFile());
        System.out.println("I removed " + getSampleNameToSample().get(sampleName).getPathToFile());
        System.out.println("Now opened: ");
        openFiles
                .forEach(System.out::println);
        samples.remove(sampleNameToSample.get(sampleName));
        sampleNameToSample.remove(sampleName);
        treeViewFiles.getRoot().getChildren().remove(indexOfTreeItem, ++indexOfTreeItem);
    }

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
