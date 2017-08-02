package UI;

import javafx.fxml.FXML;
import javafx.stage.DirectoryChooser;
import main.GlobalConstants;
import main.Main;
import main.UserSettings;
import util.SaveAndLoadOptions;

import java.io.File;

/**
 * <h1>This is the controller for the options/settings menu</h1>
 * <p>
 * Implements all methods required for the buttons for the settings menu.
 * Also accesses the userSettings
 * </p>
 *
 * @see UserSettings
 */
public class OptionsController {

    @FXML
    /**
     * changes the UI from dark to light mode
     * //TODO possibly add a warning when triggered
     */
    private void changeDarkLightMode() throws Exception {
        if ((Boolean) UserSettings.userSettings.get("theme")){
            Main.getPrimaryStage().getScene().getStylesheets().remove(0);
            Main.getPrimaryStage().getScene().getStylesheets().add(GlobalConstants.LIGHTTHEME);
            MainStageController.getOptionsStage().getScene().getStylesheets().clear();
            MainStageController.getOptionsStage().getScene().getStylesheets().add(GlobalConstants.LIGHTTHEME);
            UserSettings.userSettings.put("theme", false);
        } else {
            Main.getPrimaryStage().getScene().getStylesheets().remove(0);
            Main.getPrimaryStage().getScene().getStylesheets().add(GlobalConstants.DARKTHEME);
            MainStageController.getOptionsStage().getScene().getStylesheets().clear();
            MainStageController.getOptionsStage().getScene().getStylesheets().add(GlobalConstants.DARKTHEME);
            UserSettings.userSettings.put("theme", true);
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
        String fileLocation = file.getAbsolutePath();

        UserSettings.userSettings.put("isDefaultFileChooserLocation", false);
        UserSettings.userSettings.put("defaultFileChooserLocation", fileLocation);
    }

    @FXML
    /**
     * saves the settings
     */
    private void saveSettings(){
        SaveAndLoadOptions.saveSettings();
    }


}
