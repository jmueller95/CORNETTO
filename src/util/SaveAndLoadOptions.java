package util;

import main.GlobalConstants;
import main.UserSettings;

import java.io.*;

public class SaveAndLoadOptions {

    private static final String SEPARATOR = System.getProperty("line.separator");
    private static final String FILELOCATION = "/res/test.txt";

    /**
     * writes the settings into a custom txt file
     * '<' specifies header line
     */
    public static void saveSettings() {
        //create settingsHashmap if it doesn't exist yet
        if (UserSettings.userSettings.isEmpty()) {
            UserSettings.addUserSettings();
        }
        //save the content of the Usersettings
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FILELOCATION)))) {
            writer.write("< This file stores the settings of the" + GlobalConstants.NAME_OF_PROGRAM + SEPARATOR);

            writer.write("theme = " + UserSettings.userSettings.get("theme") + SEPARATOR);
            writer.write("defaultFileChooserDirectory = " + UserSettings.userSettings.get("defaultFileChooserDirectory") + SEPARATOR);
            writer.write("defaultFilechooserLocation = " + UserSettings.userSettings.get
                    ("defaultFilechooserLocation"));

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
        File file = new File(FILELOCATION);
        if (file != null) {
            String line;
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(FILELOCATION)))) {
                while ((line = reader.readLine()) != null) {
                    //we read the header line
                    if (line.startsWith("<")) {
                        continue;
                    } else {
                        String[] settings = line.split("=");
                        trimStrings(settings);

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

    private static String[] trimStrings(String[] stringArrayToTrim) {
        for (String string : stringArrayToTrim) {
            string.trim();
        }
        return stringArrayToTrim;
    }
}
