package main;

import UI.MainStageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 * Created by Zeth on 08.06.2017.
 * launches the Main method
 */
public class Main extends Application {
    private static Stage pStage;
    private static Parent content;
    private static Scene mainScene;

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
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource("UI/mainStageGui.fxml"));
        content = loader.load();
        primaryStage.setTitle("Network Analysis Tool");
        setXEvent(primaryStage);
        mainScene = new Scene(content, 900, 700);
        mainScene.getStylesheets().add(UserSettings.whatTheme((Boolean) UserSettings.userSettings.get("theme")));
        primaryStage.setScene(mainScene);
        primaryStage.getIcons().add(new Image(GlobalConstants.ICON));
        primaryStage.show();
    }

    /**
     * called when platform.exit is invoked
     */
    @Override
    public void stop() {
        Platform.exit();
    }

    public static Stage getPrimaryStage() {
        return pStage;
    }


    /**
     * sets the X close button to prompting an alert box
     *
     * @param stage the stage which the X event should change be set
     */
    private static void setXEvent(Stage stage) {
        MainStageController mainStageController = new MainStageController();
        stage.setOnCloseRequest(mainStageController.confirmCloseEventHandler);
    }

}