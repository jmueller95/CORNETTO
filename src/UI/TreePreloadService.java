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

import java.util.ArrayList;

import static UI.MainStageController.NAMES_DMP_SRC;
import static UI.MainStageController.NODES_DMP_SRC;
import static UI.MainStageController.setUpRequiredFiles;

/**
 * Created by Zeth on 26.06.2017.
 */
public class TreePreloadService extends Service<Void> {
    public static TaxonTree taxonTree;
    private MainStageController mainStageController = new MainStageController();
    private ProgressBar bar = mainStageController.progressBar;

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int maxPercentage = 100;

                //build the tree
                System.out.println("Downloading files...");
                setUpRequiredFiles();
                System.out.println("Parsing tree...");
                TreeParser treeParser = new TreeParser();
                treeParser.parseTree(NODES_DMP_SRC, NAMES_DMP_SRC);
                taxonTree = treeParser.getTaxonTree();
                mainStageController.initializeTreeView();
                mainStageController.textAreaDetails.setEditable(false);
                mainStageController.openFiles = new ArrayList<>();

                bar.progressProperty().bind(this.progressProperty());

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
}