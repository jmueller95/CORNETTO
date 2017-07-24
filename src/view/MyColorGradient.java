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
//private    static int factor;
/*define colors
    Color Red = Color.RED;
    Color Green = Color.GREEN;
    Color Pink = Color.PINK;
    Color Blue = Color.BLUE;
    MultipleGradientPaint.CycleMethod Reflect = MultipleGradientPaint.CycleMethod.REFLECT;
*/



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


  /*  public static void main(String[] args) {
        // launch(args);
    }
*/

/*
    @Override
    //using Linear Gradient
    public void start(Stage r) {


        //color stop-list for the gradient
        Stop Stop1 = new Stop(0, Red);
        Stop Stop2 = new Stop(1, Green);
        Stop Stop3 = new Stop(2, Pink);
        Stop[] end = new Stop[]{Stop1, Stop2, Stop3};

        //constructs the color Gradient
        LinearGradient linearGradient = new LinearGradient(2, 0, 1, 0, true, Reflect, end);

        //sets the frame
        VBox boxFrame = new VBox();
        Scene frame = new Scene(boxFrame, 200, 200);
        frame.setFill(null);

        //defines the square
        Rectangle rectangle = new Rectangle(0, 0, 200, 200);
        rectangle.setFill(linearGradient);


        //put the color gradient in the frame
        boxFrame.getChildren().add(rectangle);

        r.setScene(frame);
        r.show();
    }

    private final static int LOW = 0;
    private final static int HIGH = 300;
    private final static int HALF = (HIGH + 1) / 2;

    private static int factor;

    private static void initList(final HashMap<Integer, Color> localMap) {
        java.util.List<Integer> list = new ArrayList<Integer>(localMap.keySet());
        Collections.sort(list);
        Integer min = list.get(0);
        Integer max = list.get(list.size() - 1);
        factor = max + 1;
    }
*/
}






