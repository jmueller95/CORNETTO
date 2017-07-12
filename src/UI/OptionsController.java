package UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import main.Main;

import java.io.File;
import java.net.URL;
import java.util.Optional;

/**
 * Created by Zeth on 22.06.2017.
 */
public class OptionsController {
    @FXML
    /**
     * changes the UI from dark to light mode
     * //TODO possibly add a warning when triggered
     */
    private void changeDarkLightMode() throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL("file:"+new File("").getCanonicalPath().concat("/src/UI/mainStageGui.fxml")));
        Parent newParent;
        newParent = loader.load();
        newParent.getStylesheets().add("/UI/lightGuiStyle.css");
        Stage newStage = Main.getPrimaryStage();
        newStage.setScene(new Scene(newParent, 1000, 700));
    }

    @FXML
    /**
     * changes the default location for loading files
     *
     */
    //TODO possibly refactor this using a directory chooser
    //TOOD possibly add try/catch
    private void setNewDefaultOpenDirectory(){
        //create dialogBox
        TextInputDialog dialog = new TextInputDialog("Set new Default Location");
        dialog.setTitle("Change default location");
        dialog.setHeaderText("Change default location");
        dialog.setContentText("Please enter the new default location with an explicit path");
        Optional<String> result = dialog.showAndWait();

        String newLocation = "";
        if (result.isPresent()){
            newLocation = result.get();
        }

        MainStageController.isDefaultDirectoryLocation = false;
        MainStageController.defaultLocation = newLocation;

    }

}
