package view;

import javafx.scene.paint.Color;
import java.util.EnumSet;

/**
 * <h1>This class provides ColourBREWER colours as hexColors and JavaFX Colours.</h1>
 * <p>
 * Colour values are taken from the JavaScript library D3 https://github.com/d3/d3-scale-chromatic.
 * There are currently three color sets:
 * DIV
 * CATEGORICAL
 * SEQ
 * </p>
 *
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
    Spectral("9e0142d53e4ff46d43fdae61fee08bffffbfe6f598abdda466c2a53288bd5e4fa2"),

    // Categorical:
    Set1("e41a1c377eb84daf4a984ea3ff7f00ffff33a65628f781bf999999"),
    Set2("66c2a5fc8d628da0cbe78ac3a6d854ffd92fe5c494b3b3b3"),
    Set3("8dd3c7ffffb3bebadafb807280b1d3fdb462b3de69fccde5d9d9d9bc80bdccebc5ffed6f"),
    Accent("7fc97fbeaed4fdc086ffff99386cb0f0027fbf5b17666666"),
    Dark2("1b9e77d95f027570b3e7298a66a61ee6ab02a6761d666666"),
    Paired("a6cee31f78b4b2df8a33a02cfb9a99e31a1cfdbf6fff7f00cab2d66a3d9affff99b15928"),
    Pastel1("fbb4aeb3cde3ccebc5decbe4fed9a6ffffcce5d8bdfddaecf2f2f2"),
    Pastel2("b3e2cdfdcdaccbd5e8f4cae4e6f5c9fff2aef1e2cccccccc"),

    // Sequential:
    BuGn("f7fcfde5f5f9ccece699d8c966c2a441ae76238b45006d2c00441b"),
    PuBu("fff7fbece7f2d0d1e6a6bddb74a9cf3690c00570b0045a8d023858"),
    YlGnBu("ffffd9edf8b1c7e9b47fcdbb41b6c41d91c0225ea8253494081d58"),
    OrRd("fff7ecfee8c8fdd49efdbb84fc8d59ef6548d7301fb300007f0000");


    // Create Subsets for Filtering the Palette by Distribution Attributes
    public static final EnumSet<Palette> DIV = EnumSet.of(BrBG, RdBu, PRGn, PiYG, PuOR, RdGy, RdYlBu, RdYlGn, Spectral);
    public static final EnumSet<Palette> CATEGORICAL = EnumSet.of(Set1, Set2, Set3, Accent, Dark2, Paired, Pastel1, Pastel2);
    public static final EnumSet<Palette> SEQ = EnumSet.of(BuGn, PuBu, YlGnBu, OrRd);

    public String hexString;

    /**
     * Copnstructor
     * @param hexString
     */
    Palette(String hexString) {
        this.hexString = hexString;
    }


    /**
     * Getter method for Colours as string array
     * @return String Array with all Values of this Palette in HexCode
     */
    public String[] get() {
        int n = hexString.length() / 6;
        String[] colours = new String[n];

        for (int i = 0; i < n; i++) {
            colours[i] =  '#' + hexString.substring(i*6, (i+1)*6);
        }
        return colours;
    }

    /**
     * Getter Method for colours as JavaFX colour array
     * @return Color Array for use with JAVAFX
     */
    public Color[] getColours() {

        int n = hexString.length() / 6;
        Color[] colours = new Color[n];

        for (int i = 0; i < n; i++) {
            colours[i] =  Color.web('#'+hexString.substring(i*6, (i+1)*6));
        }
        return colours;
    }
}