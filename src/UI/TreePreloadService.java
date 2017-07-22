package UI;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import main.GlobalConstants;
import model.TaxonTree;
import treeParser.TreeParser;
import util.DownloadNodesAndNameDMPFiles;

import java.io.File;


/**
 * Created by Zeth on 26.06.2017.
 *
 * service which preloads the tree
 */
public class TreePreloadService extends Service<Void> {
    public static TaxonTree taxonTree;

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                updateMessage("Checking Required Files");
                boolean filesPresent = checkFilePresence();

                if (!filesPresent) {
                    updateMessage("Downloading Files (may take a while)");
                    DownloadNodesAndNameDMPFiles.downloadNamesNodesDMPandUnzip();
                }

                TreeParser treeParser = new TreeParser();
                treeParser.progressProperty.addListener((a, o, n) -> updateMessage("Constructing Tree: " + n));
                treeParser.parseTree(GlobalConstants.NODES_DMP_SRC, GlobalConstants.NAMES_DMP_SRC);
                taxonTree = treeParser.getTaxonTree();
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("Done!");
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                updateMessage("Cancelled!");
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("Failed!");
            }
        };

    }

    /**
     * checks if nodes.dmp and names.dmp are in the file system
     * @return
     */
    private boolean checkFilePresence() {
        File nodesDmp = new File(GlobalConstants.NODES_DMP_SRC);
        File namesDmp = new File(GlobalConstants.NAMES_DMP_SRC);

        return nodesDmp.exists() && namesDmp.exists() && !nodesDmp.isDirectory() && !namesDmp.isDirectory();
    }
}