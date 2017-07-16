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

    // Basic variables
    MyVertex myVertex;
    Circle vertexShape;
    Label vertexLabel;
    private double downX;
    private double downY;

    // Stylistic variables
    Double vertexWeight = 20.0;
    Color fillColor = Color.BEIGE;
    Color strokeColor = Color.DARKBLUE;
    Color selectedFillColor = Color.DARKORANGE;

    public MyVertexView(MyVertex myVertex) {
        this.myVertex = myVertex;
        vertexShape = new Circle(vertexWeight);
        vertexShape.setFill(fillColor);
        vertexShape.setStroke(strokeColor);

        translateXProperty().bindBidirectional(myVertex.xCoordinatesProperty());
        translateYProperty().bindBidirectional(myVertex.yCoordinatesProperty());
        addSelectionMarker();

        getChildren().add(vertexShape);

    }

    /**
     * Add mouse drag event to move nodeViews inside the parent group manually
     * Bidirectional bind also updates coordinates in node class
     */

    public void addSelectionMarker() {
        myVertex.isSelectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                vertexShape.setFill(selectedFillColor);
                System.out.println("node selected");
            } else {
                vertexShape.setFill(fillColor);
                System.out.println("node unselected");
            }
        }));
    }

    public void addNodeTransition() {


    }

    public MyVertex getMyVertex() {
        return myVertex;
    }
}
