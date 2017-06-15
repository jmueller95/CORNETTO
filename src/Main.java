import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.TaxonTree;
import treeParser.TreeParser;
import util.DownloadFilesHelper.DownloadNodesAndNameDMPFiles;

import java.io.File;

/**
 * Created by Zeth on 08.06.2017.
 * launches the Main method
 */
//TODO doubt that it should extend Application later on
public class Main extends Application {

    /**
     * the Main method
     *
     * @param args
     */
    public static void main(String args[]) {
        //launch the GUI
        launch(args);
    }

    /**
     * loads the fxml file and builds the scene
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/UI/GUI.fxml"));
        Parent content = loader.load();
        content.getStylesheets().add("/UI/GUIStyle.css");
        primaryStage.setTitle("Network Analysis Tool");
        primaryStage.setScene(new Scene(content, 800, 500));
        primaryStage.show();
    }


}
