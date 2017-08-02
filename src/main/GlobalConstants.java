package main;

import java.util.Random;

/**
 * <h1>The class defines heavily used constants - no specific usage cases</h1>
 * <p>
 * This class contains constants that are used throughout the program. The constants do not have a specific usage domain,
 * therefore any constant which is not local to a class can be defined here.
 * </p>
 *
 */
public class GlobalConstants {

    public static final String NAME_OF_PROGRAM = "CORNETTO - Correlation Network Tool";
    public static final String ICON = "images/croissant1600.png";
    public static final String DARKTHEME = "darkGuiStyle.css";
    public static final String LIGHTTHEME = "lightGuiStyle.css";

    public static final String USER_SETTINGS_FILEPATH = "./res/userSettings.txt";

    public static final String NODES_DMP_SRC = "./res/nodes.dmp";
    public static final String NAMES_DMP_SRC = "./res/names.dmp";

    // Shared Random number generator, used for creating consistent numbers with user defined seeds
    public static Random globalRandomInstance = new Random();

}
