package view;

import graph.MyVertex;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by caspar on 19.06.17.
 */
public class MyVertexView extends Group {

    MyVertex myVertex;
    Circle vertexShape;
    Label vertexLabel;
    Double vertexWeight = 20.0;
    private double deltaX;
    private double deltaY;

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
        this.setOnMouseClicked( me -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                deltaX = translateXProperty().getValue() - me.getSceneX();
                deltaY = translateYProperty().getValue() - me.getSceneY();

            }
        });

        this.setOnMouseDragged(me -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                translateXProperty().setValue(me.getSceneX() + deltaX);
                translateYProperty().setValue(me.getSceneY() + deltaY);
            }
        });
    }
}
