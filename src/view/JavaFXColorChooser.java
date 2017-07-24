package view;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by NantiaL on 24.07.2017.
 */
public class JavaFXColorChooser extends MyColorGradient{
    private Map<String, Color> colorMap = getJavaFXColorMap();
    /**
     * Return a map with all possible colors in JavaFX. Value contains an instance of a color object and the
     * key is the static name
     */

    public Map<String, Color> getJavaFXColorMap() {
        Field[] fields = java.awt.Color.class.getDeclaredFields();
        Map<String, Color> color = new HashMap<>();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                try {
                    color.put(field.getName(), (Color) field.get(null));
                } catch (NoSuchFieldError | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(JavaFXColorChooser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return color;
    }
}
