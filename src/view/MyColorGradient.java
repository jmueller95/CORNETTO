package view;


import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Created by NantiaL on 02.07.2017.
 */

public class MyColorGradient {

    private static class MyLabel extends JLabel{

        Color TopL;
        Color TopR;
        Color BottomL;
        Color BottomR;
        Color Middle;
        int size;


            public MyLabel(int size, Color leftTop, Color rightTop,Color middle ,Color leftBottom, Color rightBottom){
                super();
                this.size = size;
                this.TopL = leftTop;
                this.TopR = rightTop;
                this.BottomL = leftBottom;
                this.BottomR = rightBottom;
                this.Middle = middle;
                this.setPreferredSize(new Dimension(size, size));
            }

            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint twoColorGradient = new GradientPaint(
                        size, 0f, TopR, 0, size, BottomL);

                float radius = size-(size/4);
                float[] dist = {0f, 1.0f};
                Point2D center = new Point2D.Float(0f, 0f);
                Color noColor = new Color(0f, 0f, 0f, 0f);
                Color[] colors = {TopL, noColor};
                RadialGradientPaint thirdColor = new RadialGradientPaint(center, radius, dist, colors);


                center = new Point2D.Float(size, size);
                Color[] colors2 = {BottomR, noColor};
                RadialGradientPaint fourthColor = new RadialGradientPaint(center, radius, dist, colors2);
                RadialGradientPaint fifthColor = new RadialGradientPaint(center, radius, dist, colors2);


                //add colors
                g2d.setPaint(twoColorGradient);
                g2d.fillRect(0, 0, size, size);

                g2d.setPaint(thirdColor);
                g2d.fillRect(0, 0, size, size);

                g2d.setPaint(fourthColor);
                g2d.fillRect(0, 0, size, size);

                g2d.setPaint(fifthColor);
                g2d.fillRect(0, 0, size, size);
            }
        }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Frame");
        JPanel myPanel = new JPanel();
        frame.add(myPanel);

        myPanel.add(new MyLabel(400, Color.RED, Color.RED, Color.BLUE, Color.PINK,Color.GREEN));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    }






