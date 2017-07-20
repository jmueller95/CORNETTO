package main;

import java.util.HashMap;

public class UserSettings {

    public static String defaultFilechooserLocation = "";

    public static boolean isDarkTheme = true;
    public static boolean isDefaultDirectoryLocation = true;

    //contains all userSettings
    public static HashMap<String, Object> userSettings = new HashMap<>();

    public static void addUserSettings(){
        //add Strings
        userSettings.put("defaultFilechooserLocation", defaultFilechooserLocation);

        //add Booleans
        userSettings.put("theme", isDarkTheme);
        userSettings.put("defaultFileChooserDirectory", isDefaultDirectoryLocation);
    }

    public static String whatTheme(boolean theme){
        if (theme){
            return GlobalConstants.DARKTHEME;
        } else {
            return GlobalConstants.LIGHTTHEME;
        }
    }

}
