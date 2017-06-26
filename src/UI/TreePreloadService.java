package UI;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.TaxonTree;
import treeParser.TreeParser;

import java.util.ArrayList;

import static UI.MainStageController.NAMESDMPSRC;
import static UI.MainStageController.NODESDMPSRC;
import static UI.MainStageController.setUpRequiredFiles;

/**
 * Created by Zeth on 26.06.2017.
 */
public class TreePreloadService extends Service<Void> {
    public static TaxonTree taxonTree;
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int maxPercentage = 100;
                MainStageController mainStageController = new MainStageController();

                //build the tree
                System.out.println("Downloading files...");
                setUpRequiredFiles();
                System.out.println("Parsing tree...");
                TreeParser treeParser = new TreeParser();
                treeParser.parseTree(NODESDMPSRC, NAMESDMPSRC);
                taxonTree = treeParser.getTaxonTree();
                mainStageController.initializeTreeView();
                mainStageController.textAreaDetails.setEditable(false);
                mainStageController.openFiles = new ArrayList<>();

                for (int i = 1; i <= maxPercentage; i++) {
                    if (isCancelled()) {
                        break;
                    }

                    updateProgress(i, maxPercentage);
                    updateMessage(String.valueOf(i));

                    Thread.sleep(100);
                }
                return null;
            }
        };
    }
}


