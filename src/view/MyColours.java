package view;


import javafx.scene.paint.Color;


public class MyColours {

    /**
     * Calculates B-Spline (factor 4)
     * Code adapted from the D3.interpolate package:
     * https://github.com/d3/d3-interpolate/blob/master/src/basis.js
     * @param t1
     * @param v0
     * @param v1
     * @param v2
     * @param v3
     * @return
     */
    private static double basis (double t1, double v0, double v1, double v2, double v3) {

        double t2 = t1 * t1;
        double t3 = t1 * t1 * t1;

        double res =  ((1 - 3 * t1 + 3 * t2 - t3) * v0
                + (4 - 6 * t2 + 3 * t3) * v1
                + (1 + 3 * t1 + 3 * t2 - 3 * t3) * v2
                + t3 * v3) / 6;
        return res;
    }

    /**
     * Calculate Interpolation for vector containing values
     * @param values to interpolate in integer (rgb values)
     * @param t
     * @return
     */
    private  static int calculateSpline(int[] values, double t) {

        int n = values.length - 1;
        int v0;
        int v1;
        int v2;
        int v3;
        // Check if t = [0, 1]
        int i = 0;
        if (t <= 0) {
            t = 0;
            i = 0;
        }
        if (t >= 1) {
            t = 1;
            i = n - 1;
        } else {
            i = (int) Math.floor(t * n);
        }

        // v1
        v1 = values[i];
        // v2
        v2 = values[i + 1];
        // v0
        if (i > 0) {
            v0 = values[i - 1];
        } else {
            v0 = (2 * v1) - v2;
        }
        // v3
        if (i < n - 1) {
            v3 = values[i + 2];
        } else {
            v3 = (2 * v2) - v1;
        }

        // Calculate basis
        double t0 = ((t - i / n) * n) - i;
        return (int) Math.round(basis(t0, v0, v1, v2, v3));
    }


    /**
     * Split HexaDecimal colors into RGB and apply interpolation
     * @param palette colour scheme to use
     * @param t value [0:1] in which to interpolate
     * @return
     */
    public static Color interpolate(Palette palette, double t) {

        String[] colors = palette.get();

        int n = colors.length;
        int[] r = new int[n];
        int[] g = new int[n];
        int[] b = new int[n];

        for (int i = 0; i < n; i++) {
            r[i] = Integer.valueOf(colors[i].substring(1, 3), 16);
            g[i] = Integer.valueOf(colors[i].substring(3, 5), 16);
            b[i] = Integer.valueOf(colors[i].substring(5, 7), 16);

        }

        int smoothR = Math.min(calculateSpline(r, t), 255);
        int smoothG = Math.min(calculateSpline(g, t), 255);
        int smoothB = Math.min(calculateSpline(b, t), 255);

        return Color.rgb(smoothR, smoothG, smoothB);
    }

}






