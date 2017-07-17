package UI;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import main.GlobalConstants;
import main.Main;

import java.io.File;

/**
 * Created by Zeth on 22.06.2017.
 */
public class OptionsController {

    @FXML
    /**
     * changes the UI from dark to light mode
     * //TODO possibly add a warning when triggered
     */
    private void changeDarkLightMode() throws Exception {
        Scene scene = Main.getPrimaryStage().getScene();
        scene.getStylesheets().remove(GlobalConstants.DARKTHEME);
        scene.getStylesheets().add(GlobalConstants.LIGHTTHEME);
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
