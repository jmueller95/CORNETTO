package model;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.ArrayList;

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
 * @see AnalysisData
 */
public class LoadedData {

    private static ArrayList<Sample> samples, openFiles;

    public static void addSamplesToDatabase(ArrayList<Sample> samples, TreeView<String> treeViewFiles) {
        LoadedData.samples = samples;
        updateTreeView(treeViewFiles);
    }

    /**
     *
     * @param treeViewFiles
     */
    public static void closeProject(TreeView<String> treeViewFiles) {
        samples.clear();
        updateTreeView(treeViewFiles);
    }

    /**
     * <h1>Update the visible samples / taxa listing</h1>
     * Overwrites the treeView fxml element in the mainStageController with all elements of the samples ArrayList
     * @param treeViewFiles
     */
    private static void updateTreeView(TreeView<String> treeViewFiles) {
        TreeItem<String> newSample, newRoot, newRootID;

        int count = 0, samplesLength = samples.size();

        for (Sample sample : samples) {
            newSample = new TreeItem<>("name");
            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                String[] name = taxonNode.getName().split(".");
                newRoot = new TreeItem<>(name.length == 0 ? taxonNode.getName() : name[0]);
                newSample.getChildren().add(newRoot);

                newRootID = new TreeItem<>("" + taxonNode.getTaxonId());
                newRoot.getChildren().add(newRootID);
            }
            treeViewFiles.getRoot().getChildren().add(newSample);
        }
    }

    // GETTERS
    public static ArrayList<Sample> getSamples() {
        return samples;
    }

    // SETTERS
    public static void setSamples(ArrayList<Sample> samples) {
        LoadedData.samples = samples;
    }
}
