package UI;

import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import main.GlobalConstants;
import main.Main;
import main.UserSettings;
import util.SaveAndLoadOptions;

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
        if (UserSettings.isDarkTheme){
            Main.getPrimaryStage().getScene().getStylesheets().remove(0);
            Main.getPrimaryStage().getScene().getStylesheets().add(GlobalConstants.LIGHTTHEME);
            MainStageController.getOptionsStage().getScene().getStylesheets().clear();
            MainStageController.getOptionsStage().getScene().getStylesheets().add(GlobalConstants.LIGHTTHEME);
            UserSettings.isDarkTheme = false;
        } else {
            Main.getPrimaryStage().getScene().getStylesheets().remove(0);
            Main.getPrimaryStage().getScene().getStylesheets().add(GlobalConstants.DARKTHEME);
            MainStageController.getOptionsStage().getScene().getStylesheets().clear();
            MainStageController.getOptionsStage().getScene().getStylesheets().add(GlobalConstants.DARKTHEME);
            UserSettings.isDarkTheme = true;
        }

    }

    @FXML
    /**
     * changes the default location for loading files
     *
     */
    private void setNewDefaultOpenDirectory(){
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(null);

        UserSettings.isDefaultDirectoryLocation = false;
        UserSettings.defaultFilechooserLocation = file.getAbsolutePath();
    }

    @FXML
    /**
     * saves the settings
     */
    private void saveSettings(){
        SaveAndLoadOptions.saveSettings();
    }


}
