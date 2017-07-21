package main;

import java.io.File;
import java.util.HashMap;


public class UserSettings {

    public static File defaultFileChooserLocation = new File(System.getProperty("user.home"));

    public static boolean isDarkTheme = true;
    public static boolean isDefaultDirectoryLocation = true;

    //contains all userSettings
    public static HashMap<String, Object> userSettings = new HashMap<>();

    public static void addUserSettings(){
        //add Strings
        userSettings.put("defaultFileChooserLocation", defaultFileChooserLocation);

        //add Booleans
        userSettings.put("theme", isDarkTheme);
        userSettings.put("isDefaultFileChooserLocation", isDefaultDirectoryLocation);
    }

    public static String whatTheme(boolean theme){
        if (theme){
            return GlobalConstants.DARKTHEME;
        } else {
            return GlobalConstants.LIGHTTHEME;
        }
    }

}
