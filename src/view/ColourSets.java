package view;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NantiaL on 02.07.2017.
 */
public class ColourSets {

    public List getSet1() {
        List set1 = new ArrayList();
        set1.add(Color.BEIGE);
        set1.add(Color.DARKBLUE);
        set1.add(Color.PALETURQUOISE);
        return set1;
    }

    public List getSet2() {
        List set2 = new ArrayList();
        set2.add(Color.BLACK);
        set2.add(Color.FIREBRICK);
        set2.add(Color.RED);
        return set2;
    }

    public List getSet3() {
        List set3 = new ArrayList();
        set3.add(Color.PINK);
        set3.add(Color.GREEN);
        set3.add(Color.WHITE);
        return set3;
    }

    public List getSet4() {
        List set4 = new ArrayList();
        set4.add(Color.GRAY);
        set4.add(Color.GOLD);
        set4.add(Color.AQUA);
        return set4;
    }

}

