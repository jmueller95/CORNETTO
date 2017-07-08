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

    private static ArrayList<Sample> samples, openFiles;
    private static MyGraph<MyVertex, MyEdge> taxonGraph;

    public static void addSamplesToDatabase(ArrayList<Sample> loadedSamples, TreeView<String> treeViewFiles) {
        if (samples == null) {
            samples = loadedSamples;
        } else {
            samples.addAll(loadedSamples);
        }
        updateTreeView(treeViewFiles, samples);
    }


    /**
     * Builds a fully connected graph of all taxa contained in the sample list.
     * This method must be called AFTER analysis is performed, since it needs the correlations and p-values
     */
    public static void createGraph(){
        LinkedList<TaxonNode> nodeList = SampleComparison.getUnifiedTaxonList(samples);
        taxonGraph = new MyGraph<>();

        //Create a vertex for each taxonNode
        for (TaxonNode taxonNode : nodeList) {
            taxonGraph.addVertex(new MyVertex(taxonNode));
        }

        //Connect every pair of vertices with a MyEdge, set correlation and pValue of the edge
        final HashMap<TaxonNode, MyVertex> taxonNodeToVertexMap = taxonGraph.getTaxonNodeToVertexMap();
        final RealMatrix correlationMatrix = AnalysisData.getCorrelationMatrix();
        final RealMatrix pValueMatrix = AnalysisData.getpValueMatrix();
        for (int i = 0; i < nodeList.size(); i++) {
            for (int j = 0; j < i; j++) {
                MyVertex sourceVertex = taxonNodeToVertexMap.get(nodeList.get(i));
                MyVertex targetVertex = taxonNodeToVertexMap.get(nodeList.get(j));
                MyEdge edge = new MyEdge(sourceVertex, targetVertex);
                edge.setCorrelation(correlationMatrix.getEntry(i,j));
                edge.setPValue(pValueMatrix.getEntry(i,j));
                taxonGraph.addEdge(edge, sourceVertex, targetVertex);
            }
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
     * Example method to filter the displayed samples after ID
     *
     * @param treeViewFiles
     * @param maxCount
     */
    //TODO: Introduce a useful filter value
    public static void filterTaxaAfterCount(TreeView<String> treeViewFiles, int maxCount) {
        ArrayList<Sample> filteredSamples = new ArrayList<>();
        for (Sample sample : samples) {
            HashMap<TaxonNode, Integer> filteredTaxa = new HashMap<>();
            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                int foundCount = sample.getTaxonCountRecursive(taxonNode);
                //System.out.println("Found value: " + foundCount);
                if (foundCount <= maxCount) {
                    //System.out.println("Didn't throw that away");
                    filteredTaxa.put(taxonNode, foundCount);
                }
            }
            sample.setTaxa2CountMap(filteredTaxa);
            if (!filteredSamples.isEmpty()) {
                filteredSamples.add(sample);
            }
        }
        updateTreeView(treeViewFiles, filteredSamples);
    }

    /**
     * <h1>Update the visible samples / taxa listing</h1>
     * Overwrites the treeView fxml element in the mainStageController with all elements of the samples ArrayList
     *
     * @param treeViewFiles Tree view fxml element to display the loaded samples
     */
    private static void updateTreeView(TreeView<String> treeViewFiles, ArrayList<Sample> loadedSamples) {
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

    //TODO: Abstract the methods below to avoid repetition
    public static int getMaxId() {
        int maxId = 0;
        for (Sample sample : samples) {
            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                int foundTaxonId = taxonNode.getTaxonId();
                if (foundTaxonId > maxId) {
                    maxId = foundTaxonId;
                }
            }
        }
        return maxId;
    }

    public static int getMinId() {
        int minId = 0;
        for (Sample sample : samples) {
            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                int foundTaxonId = taxonNode.getTaxonId();
                if (foundTaxonId < minId) {
                    minId = foundTaxonId;
                }
            }
        }
        return minId;
    }

    public static int getMaxCount() {
        int maxCount = 0;
        for (Sample sample : samples) {
            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                int foundTaxonCount = sample.getTaxonCountRecursive(taxonNode);
                if (foundTaxonCount > maxCount) {
                    maxCount = foundTaxonCount;
                }
            }
        }
        return maxCount;
    }

    public static int getMinCount() {
        int minCount = 0;
        for (Sample sample : samples) {
            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                int foundTaxonCount = sample.getTaxonCountRecursive(taxonNode);
                if (foundTaxonCount < minCount) {
                    minCount = foundTaxonCount;
                }
            }
        }
        return minCount;
    }

    // SETTERS
    public static void setSamples(ArrayList<Sample> samples) {
        LoadedData.samples = samples;
    }
}
