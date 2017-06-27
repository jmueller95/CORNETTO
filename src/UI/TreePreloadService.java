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
                treeParser.parseTree(NODES_DMP_SRC, NAMES_DMP_SRC);
                taxonTree = treeParser.getTaxonTree();
                mainStageController.initializeTreeView();
                mainStageController.textAreaDetails.setEditable(false);
                mainStageController.openFiles = new ArrayList<>();

//                //build GUI for showing the progress
//                Group root = new Group();
//                Stage downloadingFilesStage = new Stage();
//                downloadingFilesStage.setTitle("Parsing tree...");
//                downloadingFilesStage.setScene(new Scene(root, 330, 120, Color.WHITE));
//                downloadingFilesStage.show();
//
//                BorderPane mainPane = new BorderPane();
//                root.getChildren().add(mainPane);
//
//                final Label label = new Label("Parsing Tree...");
//                final ProgressBar progressBar = new ProgressBar(0);
//
//                final HBox hb = new HBox();
//                hb.setSpacing(5);
//                hb.setAlignment(Pos.CENTER);
//                hb.getChildren().addAll(label, progressBar);
//                mainPane.setTop(hb);
//
//                final HBox hb2 = new HBox();
//                hb2.setSpacing(5);
//                hb2.setAlignment(Pos.CENTER);
//                mainPane.setBottom(hb2);
//
//                //setup progress bar
//                progressBar.setProgress(0);
//                progressBar.progressProperty().bind(progressBar.progressProperty());

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