package Main;

import UI.MainStageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URL;
import java.util.Optional;


/**
 * Created by Zeth on 08.06.2017.
 * launches the Main method
 */
public class Main extends Application {
    private static Stage pStage;

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
        pStage = primaryStage;
        //TODO this is not working yet
        //should do the animation while setting up files
        //StartUpAnimation startUpAnimation = new StartUpAnimation();
        //startUpAnimation.startAnimation(primaryStage);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL("file:"+new File("").getCanonicalPath().concat("/src/UI/mainStageGui.fxml")));
        Parent content = loader.load();
        content.getStylesheets().add("/UI/GuiStyle.css");
        primaryStage.setTitle("Network Analysis Tool");
        setXEvent(primaryStage);
        primaryStage.setScene(new Scene(content, 900, 700));
        primaryStage.show();
    }

    /**
     * called when platform.exit is invoked
     *
     * */
    @Override
    public void stop(){
        Platform.exit();
    }

    public static Stage getPrimaryStage() {
        return pStage;
    }

    private void setPrimaryStage(Stage pStage) {
        this.pStage = pStage;
    }

    private static void setXEvent(Stage primaryStage){
        MainStageController mainStageController = new MainStageController();
        primaryStage.setOnCloseRequest(mainStageController.confirmCloseEventHandler);
    }


}