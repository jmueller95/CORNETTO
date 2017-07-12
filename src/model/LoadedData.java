package model;

import analysis.SampleComparison;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * <h1>Class that stores data parsed from the loaded files</h1>
 * The TreeView Element inside of the MainStageController is now separated
 * from the actual data which is stored in the 'samples' field.
 * Operations on the data that is presented in the 'samples' field of
 * this class is placed here as well.
 * <p>
 * <b>Note:</b> This class is not intended to store methods or data
 * that is connected with analysis. The AnalysisData class has been
 * created for this purpose those.
 *
 * @see AnalysisData
 */
public class LoadedData {

    private static ArrayList<Sample> samples;
    private static ArrayList<String> openFiles;
    private static MyGraph<MyVertex, MyEdge> taxonGraph;

    public static void addSamplesToDatabase(ArrayList<Sample> loadedSamples, TreeView<String> treeViewFiles) {
        if (samples == null) {
            samples = loadedSamples;
        } else {
            samples.addAll(loadedSamples);
        }
        initializeTreeView(treeViewFiles, samples);
    }

    /**
     * Builds a fully connected graph of all taxa contained in the sample list.
     * This method must be called AFTER analysis is performed, since it needs the correlations and p-values
     * TODO: Test this, and especially test if the hashmaps are added correctly!
     */
    public static void createGraph() {
        LinkedList<TaxonNode> nodeList = SampleComparison.getUnifiedTaxonList(samples);
        taxonGraph = new MyGraph<>();

        //Create a vertex for each taxonNode
        for (TaxonNode taxonNode : nodeList) {
            MyVertex vertex = new MyVertex(taxonNode);
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
        }
    }

    /**
     * <h1>Initializes the visible samples / taxa listing</h1>
     * Overwrites the treeView fxml element in the mainStageController with all elements of the samples ArrayList
     *
     * @param treeViewFiles Tree view fxml element to display the loaded samples
     */
    private static void initializeTreeView(TreeView<String> treeViewFiles, ArrayList<Sample> loadedSamples) {
        treeViewFiles.setRoot(new TreeItem<>("root"));

        TreeItem<String> newSample, newRoot, newRootID, newRootCount;

        int count = 0;
        for (Sample sample : samples) {
            //TODO: Find a way to display the file name here without needing the fileName as parameter
            newSample = new TreeItem<>("sample " + ++count);
            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                String[] name = taxonNode.getName().split(".");
                newRoot = new TreeItem<>("name: " + (name.length == 0 ? taxonNode.getName() : name[0]));
                newSample.getChildren().add(newRoot);

                newRootID = new TreeItem<>("id: " + taxonNode.getTaxonId());
                newRootCount = new TreeItem<>("count: " + sample.getTaxonCountRecursive(taxonNode));

                newRoot.getChildren().addAll(newRootID, newRootCount);
            }
            treeViewFiles.getRoot().getChildren().add(newSample);
        }
    }

    // GETTERS
    public static ArrayList<Sample> getSamples() {
        return samples;
    }

    public static MyGraph<MyVertex, MyEdge> getTaxonGraph() {
        return taxonGraph;
    }

    // SETTERS
    public static void setSamples(ArrayList<Sample> samples) {
        LoadedData.samples = samples;
    }
}
