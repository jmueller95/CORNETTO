package view;


import com.sun.prism.j2d.paint.MultipleGradientPaint;
import javafx.scene.paint.*;

import java.awt.*;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.application.Application.getUserAgentStylesheet;
import static javafx.application.Application.launch;


/**
 * Created by NantiaL on 22.07.2017.
 */
public class MyColorGradient {

//private   final static Map<Integer, Color> map = initNumberToColorMap();
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






