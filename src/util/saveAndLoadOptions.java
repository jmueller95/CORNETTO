package util;

import UI.MainStageController;
import main.GlobalConstants;

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

            writer.write("theme = " + String.valueOf(MainStageController.ISDARKTHEME) + SEPARATOR);
            writer.write("default fileChooser = " + String.valueOf(MainStageController.isDefaultDirectoryLocation) +
                    SEPARATOR);

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
                        //TODO handle the read in stuff
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }
}
