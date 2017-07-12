package UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.Main;

import java.io.File;
import java.net.URL;

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
     */ private void setNewDefaultOpenDirectory(){
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(null);

        MainStageController.isDefaultDirectoryLocation = false;
        MainStageController.defaultLocation = file.getAbsolutePath();
    }

}
