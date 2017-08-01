package main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class UserSettings {

    //Strings
    private static File defaultFileChooserLocation = new File(System.getProperty("user.home"));
    private static String font;
    private static String nodesColor;

    //booleans
    private static boolean isDarkTheme = true;
    private static boolean isDefaultDirectoryLocation = true;
    private static boolean isUnconnectedEdgesHiddenOnStartup = false;

    //contains all userSettings
    public static HashMap<String, Object> userSettings = new HashMap<>();


    public static void addUserSettings(){
        File dummyFile = new File("./res/userSettings.txt");
        boolean isFileCreated = false;
        try {
            isFileCreated = dummyFile.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        }
        //add Strings
        userSettings.put("defaultFileChooserLocation", defaultFileChooserLocation.getAbsolutePath());
        userSettings.put("font", font);
        userSettings.put("nodesColor", nodesColor);

        //add Booleans
        userSettings.put("theme", isDarkTheme);
        userSettings.put("isDefaultFileChooserLocation", isDefaultDirectoryLocation);
        userSettings.put("isUnconnectedEdgesHiddenOnStartup", isUnconnectedEdgesHiddenOnStartup);
    }

    /**
     * @param theme
     * @return
     */
    public static String whatTheme(boolean theme){
        if (theme){
            return GlobalConstants.DARKTHEME;
        } else {
            return GlobalConstants.LIGHTTHEME;
        }
    }

}
