package main;

import UI.MainStageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;


/**
 * Created by Zeth on 08.06.2017.
 * launches the Main method
 */
public class Main extends Application {
    private static Stage pStage;
    private static Parent content;

    /**
     * the Main method
     *
     * @param args just the starting point - no files or other run parameters!
     */
    public static void main(String args[]) {
        //launch the GUI
        launch(args);
    }

    /**
     * loads the fxml file and builds the scene
     *
     * @param primaryStage the main window and starting point of our GUI
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
        content = loader.load();
        content.getStylesheets().add("/UI/darkGuiStyle.css");
        primaryStage.setTitle("Network Analysis Tool");
        setXEvent(primaryStage);
        primaryStage.setScene(new Scene(content, 900, 700));
      //  primaryStage.getIcons().add(new Image("images/science-icon.png"));
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

    public static Parent getContent() {
        return content;
    }

    public static void setContent(Parent content) {
        Main.content = content;
    }

    /**
     * sets the X close button to prompting an alert box
     *
     * @param stage the stage which the X event should change be set
     */
    private static void setXEvent(Stage stage){
        MainStageController mainStageController = new MainStageController();
        stage.setOnCloseRequest(mainStageController.confirmCloseEventHandler);
    }


}