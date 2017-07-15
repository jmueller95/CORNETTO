package view;

import graph.MyVertex;
import javafx.beans.property.Property;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * Created by caspar on 19.06.17.
 */
public class MyVertexView extends Group {

    MyVertex myVertex;
    Circle vertexShape;
    Label vertexLabel;
    Double vertexWeight = 20.0;
    private double downX;
    private double downY;

    public MyVertexView(MyVertex myVertex) {
        this.myVertex = myVertex;
        vertexShape = new Circle(vertexWeight);
        vertexShape.setFill(Color.BEIGE);
        vertexShape.setStroke(Color.DARKBLUE);

        translateXProperty().bindBidirectional(myVertex.xCoordinatesProperty());
        translateYProperty().bindBidirectional(myVertex.yCoordinatesProperty());
        addMouseEvent();

        getChildren().add(vertexShape);

    }



    public void addMouseEvent() {
        // Register Location of Mouse button click
        this.setOnMousePressed( me -> {
            if (me.getButton() == MouseButton.PRIMARY) {

                downX = me.getSceneX()- translateXProperty().get();
                downY = me.getSceneY() - translateYProperty().get();
                System.out.println("DownX: "+ downX);
                System.out.println("DownY: "+ downY);

            }
        });

        this.setOnMouseDragged(me -> {
            if (me.getButton() == MouseButton.PRIMARY) {

                double deltaX = me.getSceneX() - downX;
                double deltaY = me.getSceneY() - downY;

                System.out.print("    Mouse X:" + (deltaX));
                System.out.println("Mouse Y:" + (deltaY));

                translateXProperty().set(deltaX);
                translateYProperty().set(deltaY);
            }
        });
    }
}
