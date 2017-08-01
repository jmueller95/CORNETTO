package view;

import javafx.scene.paint.Color;

import java.util.EnumSet;

/**
 * Created by caspar on 29.07.17.
 */

public enum Palette {

    // Diverging:
    BrBG("5430058c510abf812ddfc27df6e8c3f5f5f5c7eae580cdc135978f01665e003c30"),
    RdBu("67001fb2182bd6604df4a582fddbc7f7f7f7d1e5f092c5de4393c32166ac053061"),
    PRGn("40004b762a839970abc2a5cfe7d4e8f7f7f7d9f0d3a6dba05aae611b783700441b"),
    PiYG("8e0152c51b7dde77aef1b6dafde0eff7f7f7e6f5d0b8e1867fbc414d9221276419"),
    PuOR("2d004b5427888073acb2abd2d8daebf7f7f7fee0b6fdb863e08214b358067f3b08"),
    RdGy("67001fb2182bd6604df4a582fddbc7ffffffe0e0e0bababa8787874d4d4d1a1a1a"),
    RdYlBu("a50026d73027f46d43fdae61fee090ffffbfe0f3f8abd9e974add14575b4313695"),
    RdYlGn("a50026d73027f46d43fdae61fee08bffffbfd9ef8ba6d96a66bd631a9850006837"),
    Spectral("9e0142d53e4ff46d43fdae61fee08bffffbfe6f598abdda466c2a53288bd5e4fa2");

    // Categorical:


    public static final EnumSet<Palette> DIV = EnumSet.of(BrBG, RdBu);

    public String hexString;

    Palette(String hexString) {
        this.hexString = hexString;
    }

    public String[] get() {
        int n = hexString.length() / 6;
        String[] colours = new String[n];

        for (int i = 0; i < n; i++) {
            colours[i] =  '#' + hexString.substring(i*6, (i+1)*6);
        }
        return colours;
    }


    public Color[] getColours() {

        int n = hexString.length() / 6;
        Color[] colours = new Color[n];

        for (int i = 0; i < n; i++) {
            colours[i] =  Color.web('#'+hexString.substring(i*6, (i+1)*6));
        }
        return colours;
    }

}