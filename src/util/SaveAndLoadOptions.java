package util;

import main.GlobalConstants;
import main.UserSettings;

import java.io.*;

public class SaveAndLoadOptions {

    private static final String SEPARATOR = System.getProperty("line.separator");

    /**
     * writes the settings into a custom txt file
     * '<' specifies header line
     */
    public static void saveSettings() {
        //create settings hashmap if it doesn't exist yet
        if (UserSettings.userSettings.isEmpty()) {
            UserSettings.addUserSettings();
        }
        //save the content of the user settings
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(GlobalConstants.USER_SETTINGS_FILEPATH)))) {
            writer.write("< This file stores the settings of the" + GlobalConstants.NAME_OF_PROGRAM + SEPARATOR);

            writer.write("theme = " + UserSettings.userSettings.get("theme") + SEPARATOR);
            writer.write("isDefaultFileChooserLocation = " + UserSettings.userSettings.get
                    ("isDefaultFileChooserLocation") + SEPARATOR);
            writer.write("defaultFileChooserLocation = " + UserSettings.userSettings.get("defaultFileChooserLocation"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * loads the settings from the txt file
     */
    public static void loadSettings() {
        //create settingsHashmap if it doesn't exist yet
        if (UserSettings.userSettings.isEmpty()) {
            UserSettings.addUserSettings();
        }
        //check if saved file exists
        File file = new File(GlobalConstants.USER_SETTINGS_FILEPATH);
        if (file != null) {
            String line;
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(GlobalConstants.USER_SETTINGS_FILEPATH)))) {
                while ((line = reader.readLine()) != null) {
                    //we read the header line
                    if (line.startsWith("<")) {
                        continue;
                    } else {
                        String[] settings = line.split("=");
                        trimStrings(settings);

                        //check whether the setting is a boolean or a string and add it to the settings
                        if (UserSettings.userSettings.containsKey(settings[0])) {
                            if (UserSettings.userSettings.get(settings[0]) instanceof Boolean) {
                                UserSettings.userSettings.put(settings[0], Boolean.valueOf(settings[1]));
                            } else if (UserSettings.userSettings.get(settings[0]) instanceof String) {
                                UserSettings.userSettings.put(settings[0], settings[1]);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void trimStrings(String[] stringArrayToTrim) {
       for (int i = 0; i < stringArrayToTrim.length; i++){
           stringArrayToTrim[i] = stringArrayToTrim[i].trim();
       }
    }
}
