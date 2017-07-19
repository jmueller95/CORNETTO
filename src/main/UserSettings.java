package main;

import java.util.HashMap;

public class UserSettings {

    public static String defaultFilechooserLocation = "";

    public static boolean isDarkTheme = true;
    public static boolean isDefaultDirectoryLocation = true;

    public static HashMap<String, Boolean> userSettings = new HashMap<>();

    private static void addUserSettings(){
        userSettings.put("theme", isDarkTheme);
        userSettings.put("defaultFileChooserDirectory", isDefaultDirectoryLocation);
    }

    public static String getDefaultFilechooserLocation() {
        return defaultFilechooserLocation;
    }

    public static void setDefaultFilechooserLocation(String defaultFilechooserLocation) {
        UserSettings.defaultFilechooserLocation = defaultFilechooserLocation;
    }
}
