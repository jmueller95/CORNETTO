import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.TaxonTree;
import treeParser.TreeParser;
import util.DownloadFilesHelper.DownloadNodesAndNameDMPFiles;

import java.io.File;

import static javafx.application.Application.launch;

/**
 * Created by Zeth on 08.06.2017.
 * launches the main method
 */
//TODO doubt that it should extend Application later on
public class main extends Application {
    private static final String NODESDMPSRC = "./res/nodes.dmp";
    private static final String NAMESDMPSRC = "./res/names.dmp";

    public static void main(String args[]) {
        //build the tree
        setUpRequiredFiles();
        TreeParser treeParser = new TreeParser();
        treeParser.parseTree(NODESDMPSRC, NAMESDMPSRC);
        TaxonTree taxonTree = treeParser.getTaxonTree();

        //launch the GUI
        launch(args);

    }

    /*
    only testing the GUI
     */
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction((event) -> System.out.println("Hello World!"));

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void setUpRequiredFiles() {

        //check if required files exist
        File nodesDmp = new File(NODESDMPSRC);
        File namesDmp = new File(NAMESDMPSRC);

        //if files do NOT exist or they are a directory
        if (!(nodesDmp.exists() && namesDmp.exists() && !nodesDmp.isDirectory() && !namesDmp.exists())) {
            DownloadNodesAndNameDMPFiles downloadNodesAndNameDMPFiles = new DownloadNodesAndNameDMPFiles();
            downloadNodesAndNameDMPFiles.DownloadNamesNodesDMPandUnzip();
        }
    }




}
