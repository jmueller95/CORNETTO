package view;


import com.sun.prism.j2d.paint.MultipleGradientPaint;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by NantiaL on 22.07.2017.
 */
public class MyColorGradient //extends Application
 {

//private   final static Map<Integer, Color> map = initNumberToColorMap();
//private    static int factor;
    //define colors
    Color Red = Color.RED;
    Color Green = Color.GREEN;
    Color Pink = Color.PINK;
    Color Blue = Color.BLUE;

    MultipleGradientPaint.CycleMethod Reflect = MultipleGradientPaint.CycleMethod.REFLECT;

/*
    @Override
        //using Linear Gradient
        public void start (Stage r){


        //color stop-list for the gradient
        Stop Stop1 = new Stop(0, Red);
        Stop Stop2 = new Stop(1, Green);
        Stop Stop3 = new Stop(2,Pink);
        Stop[] end = new Stop[]{Stop1, Stop2, Stop3};

        //constructs the color Gradient
        LinearGradient linearGradient = new LinearGradient(2, 0, 1, 0,true, Reflect, end);

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
*/
        private final static int LOW = 0;
        private final static int HIGH = 300;
        private final static int HALF = (HIGH + 1) / 2;

        private    static int factor;

        private static void initList(final HashMap<Integer, Color> localMap) {
            java.util.List<Integer> list = new ArrayList<Integer>(localMap.keySet());
            Collections.sort(list);
            Integer min = list.get(0);
            Integer max = list.get(list.size() -1);
            factor = max + 1;
        }

        /**
         * @param value
         * @return
         */
        private static int rangeCheck(final int value) {
            while (value > HIGH) {
                return HIGH;
            }
            return value;
        }


        public static Map<Integer, Color> initNumberToColorMap() {
            HashMap<Integer, Color> Map = new HashMap<Integer, Color>();

            // factor (increment or decrement)
            int factorZero = 0;
            int factorOne = 1;
            int Low1 = LOW;
            int Low2= LOW;
            int Half = HALF;

            int count = 0;
            while (true) {
                java.awt.Color color;
//            color = Map.put(count++, new Color(Low1, Low2, Half));
                if (Half == HIGH) {
                    factorZero = 1; // increment
                }
                if (Low2 == HIGH) {
                    factorOne = -1; // decrement
                    factorZero = +1; // increment
                }
                if (Low1 == HIGH || (Low2 == LOW && Half == LOW)) {
                    factorZero = -1; // decrement
                }

                if (Low1 < HALF && Half == LOW) {
                    break; // finish
                }
                Low1 += factorZero;
                Low2 += factorZero;
                Half += factorOne;
                Low1 = rangeCheck(Low1);
                Low2 = rangeCheck(Low2);
                Half = rangeCheck(Half);
            }
            initList(Map);
            return Map;
        }

        public static void main(String[] args) {
            //launch(args);
        }


}





