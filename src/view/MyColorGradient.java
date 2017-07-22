package view;

import com.sun.glass.ui.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.graphstream.ui.j2dviewer.renderer.shape.swing.SquareShape;

import java.awt.*;


/**
 * Created by NantiaL on 22.07.2017.
 */
public class MyColorGradient extends Application {

    //define colors
    Color Red = Color.RED;
    Color Green = Color.GREEN;
    Color Pink = Color.PINK;
    Color Blue = Color.BLUE;

    CycleMethod Reflect = CycleMethod.REFLECT;


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

    //show the frame
    public static void main(String[] args) {
        launch(args);
    }

}





