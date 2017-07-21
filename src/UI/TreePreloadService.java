package UI;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.TaxonTree;
import treeParser.TreeParser;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import util.DownloadNodesAndNameDMPFiles;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Zeth on 26.06.2017.
 *
 * service which preloads the tree
 */
public class TreePreloadService extends Service<Void> {
    public static TaxonTree taxonTree;
    public static final String NODES_DMP_SRC = "./res/nodes.dmp";
    public static final String NAMES_DMP_SRC = "./res/names.dmp";


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
                treeParser.progressProperty.addListener( (a, o, n) -> updateMessage("Constructing Tree: " + n));
                treeParser.parseTree(NODES_DMP_SRC, NAMES_DMP_SRC);
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

    private boolean checkFilePresence() {
        File nodesDmp = new File(NODES_DMP_SRC);
        File namesDmp = new File(NAMES_DMP_SRC);

        return nodesDmp.exists() && namesDmp.exists() && !nodesDmp.isDirectory();
    }
}