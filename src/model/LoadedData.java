package model;

import analysis.SampleComparison;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import org.apache.commons.math3.linear.RealMatrix;

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
 *
 * @see AnalysisData
 */
public class LoadedData {

    private static ObservableList<Sample> samples;
    private static ArrayList<String> openFiles;
    private static MyGraph<MyVertex, MyEdge> taxonGraph;
    private static HashMap<String, Sample> sampleNameToSample = new HashMap<>();
   private static ObservableList<Sample> selectedSamples = FXCollections.observableArrayList();
    private static BooleanProperty analyzeSelected = new SimpleBooleanProperty(false);


    public static void addSamplesToDatabase(ArrayList<Sample> loadedSamples, TreeView<String> treeViewFiles, String fileName) {
        if (samples == null) {
            samples = FXCollections.observableArrayList(loadedSamples);
        } else {
            samples.addAll(loadedSamples);
        }
        initializeTreeView(treeViewFiles, loadedSamples, fileName);
    }

    /**
     * Builds a fully connected graph of all taxa contained in the sample list.
     * This method must be called AFTER analysis is performed, since it needs the correlations and p-values
     * TODO: Test this, and especially test if the hashmaps are added correctly!
     */
    public static void createGraph() {
        LinkedList<TaxonNode> nodeList = SampleComparison.getUnifiedTaxonList(samples, AnalysisData.getLevel_of_analysis());
        taxonGraph = new MyGraph<>();

        //Create a vertex for each taxonNode
        for (TaxonNode taxonNode : nodeList) {
            MyVertex vertex = new MyVertex(taxonNode, nodeList.size()-1); //It will be a fully connected graph
            taxonGraph.addVertex(vertex);
            //Add mapping of node to vertex to hashmap
            taxonGraph.getTaxonNodeToVertexMap().put(taxonNode, vertex);
        }

        //Connect every pair of vertices with a MyEdge, set correlation and pValue of the edge
        final HashMap<TaxonNode, MyVertex> taxonNodeToVertexMap = taxonGraph.getTaxonNodeToVertexMap();
        final RealMatrix correlationMatrix = AnalysisData.getCorrelationMatrix();
        final RealMatrix pValueMatrix = AnalysisData.getPValueMatrix();
        for (int i = 0; i < nodeList.size(); i++) {
            //Create Hashmap for this index, or access it if it's already there
            HashMap<Integer, MyEdge> currentEdgeMap =
                    taxonGraph.getNodeIdsToEdgesMap().getOrDefault(nodeList.get(i).getTaxonId(), new HashMap<>());
            for (int j = 0; j < i; j++) {
                MyVertex sourceVertex = taxonNodeToVertexMap.get(nodeList.get(i));
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
            //TODO: Add open files feature
            //openFiles.clear();
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
    private static void initializeTreeView(TreeView<String> treeViewFiles, ArrayList<Sample> loadedSamples, String fileName) {
        //If no samples have been loaded so far
        if (treeViewFiles.getRoot() == null) {
            treeViewFiles.setRoot(new TreeItem<>("root"));
            //The classic treeview only has one root item, but you can work around this by just setting it to invisible
            treeViewFiles.setShowRoot(false);
            treeViewFiles.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
        }
        TreeItem<String> newRoot, newRootID, newRootCount;

        //TODO: Need to exchange the CheckBoxTreeItems with TreeItems for the 'not-sample' Items
        /*treeViewFiles.setCellFactory(item -> {
            return new CheckBoxTreeCell<String>() {

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item != null) {
                        this.editableProperty().unbind();
                        CheckBoxTreeItem<String> value = (CheckBoxTreeItem<String>) treeItemProperty().getValue();
                        this.disableProperty().bind(value.leafProperty());
                    }
                }
            };
        });*/

        int count = 0;
        for (Sample sample : loadedSamples) {
            String[] fileNameSplit = fileName.split("\\.");
            String fileNameWithoutExtension = (String.join(".", Arrays.copyOfRange(fileNameSplit, 0, fileNameSplit.length - 1)));
            String sampleName = (loadedSamples.size() > 1 ? "[" + ++count + "] " + fileNameWithoutExtension : fileNameWithoutExtension);

            CheckBoxTreeItem<String> newSample = new CheckBoxTreeItem<>(sampleName);
            newSample.selectedProperty().addListener((observable, oldValue, newValue) -> {
                selectOrDeselectSample(newValue, oldValue, newSample);
            });

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

    private static void selectOrDeselectSample(boolean newValue, boolean oldValue, CheckBoxTreeItem<String> newSample) {
        if (newValue && !oldValue) {
            selectedSamples.add(sampleNameToSample.get(newSample.getValue()));
        } else {
            selectedSamples.remove(sampleNameToSample.get(newSample.getValue()));
        }
    }

    public static ObservableList<Sample> getSamplesToAnalyze(){
        return analyzeSelected.get() ? selectedSamples : samples;
    }

    // GETTERS
    public static ObservableList<Sample> getSamples() {
        return samples;
    }

    public static MyGraph<MyVertex, MyEdge> getTaxonGraph() {
        return taxonGraph;
    }

    public static HashMap<String, Sample> getSampleNameToSample() { return sampleNameToSample; }

    public static ObservableList<Sample> getSelectedSamples() {
        return selectedSamples;
    }

    public static boolean isAnalyzeSelected() {
        return analyzeSelected.get();
    }

    public static BooleanProperty analyzeSelectedProperty() {
        return analyzeSelected;
    }



    // SETTERS
    public static void setSamples(ObservableList<Sample> samples) {
        LoadedData.samples = samples;
    }

    public static void setAnalyzeSelected(boolean analyzeSelected) {
        LoadedData.analyzeSelected.set(analyzeSelected);
    }
}
