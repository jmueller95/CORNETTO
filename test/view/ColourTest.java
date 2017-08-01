package view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.Before;


/**
 * Created by caspar on 29.07.17.
 */
public class ColourTest extends Application{

    @Before
    public void setUp() throws Exception {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Colour Test");

        FlowPane root = new FlowPane();
        root.setHgap(0);
        root.setVgap(20);
        root.setAlignment(Pos.CENTER);

        for (Color col: Palette.BrBG.getColours()) {
            System.out.println(col.toString());
            Circle circ = new Circle(10, col);
            root.getChildren().add(circ);
        }

        // Add new Line to Flowpane
        Region p = new Region();
        p.setPrefSize(Double.MAX_VALUE, 0.0);
        root.getChildren().add(p);

        for (Color col: Palette.RdBu.getColours()) {
            System.out.println(col.toString());
            Circle circ = new Circle(10, col);
            root.getChildren().add(circ);
        }

        // Add new Line to Flowpane
        Region p2 = new Region();
        p.setPrefSize(Double.MAX_VALUE, 0.0);
        root.getChildren().add(p2);

        MyColours myColours = new MyColours();

        for (int i = 0; i < 100; i++) {
            Rectangle rect = new Rectangle(2, 20, myColours.interpolate(Palette.BrBG, 0.01 * i));
            root.getChildren().add(rect);
        }

        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();


    }
}
