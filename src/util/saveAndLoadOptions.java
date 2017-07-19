package util;

import main.GlobalConstants;
import main.UserSettings;

import java.io.*;

public class saveAndLoadOptions {

    private final String SEPARATOR = System.getProperty("line.separator");
    private final String FILELOCATION = "/res/test.txt";

    /**
     * writes the settings into a custom txt file
     * '<' specifies header line
     */
    private void saveSettings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(FILELOCATION)))){
            writer.write("< This file stores the settings of the" + GlobalConstants.NAME_OF_PROGRAM + SEPARATOR);

            writer.write("theme = " + String.valueOf(UserSettings.isDarkTheme) + SEPARATOR);
            writer.write("defaultFileChooserDirectory = " + String.valueOf(UserSettings.isDefaultDirectoryLocation) +
                    SEPARATOR);
            writer.write("defaultFilechooserLocation = " + UserSettings.defaultFilechooserLocation);

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * loads the settings from the txt file
     */
    private void loadSettings() {
        //check if saved file exists
        File file = new File(FILELOCATION);
        String line = "";
        if(file != null){
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(FILELOCATION)))){
                while ((line = reader.readLine()) != null){
                    //we read the header line
                    if (line.startsWith("<")){
                        continue;
                    } else {
                        String[] settings = line.split("=");
                        trimStrings(settings);
                        if (UserSettings.userSettings.containsKey(settings[0])){
                            UserSettings.userSettings.put(settings[0], Boolean.valueOf(settings[1]));
                        } else if (settings[0].equals("defaultFilechooserLocation")){
                            UserSettings.setDefaultFilechooserLocation(settings[1]);
                        }
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private String[] trimStrings(String[] stringArrayToTrim){
        for (String string: stringArrayToTrim) {
            string.trim();
        }
        return stringArrayToTrim;
    }
}
