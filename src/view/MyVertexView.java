package view;

import graph.MyVertex;
import javafx.scene.Group;
import javafx.scene.control.Label;
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

    public MyVertexView(MyVertex myVertex) {
        this.myVertex = myVertex;
        vertexShape = new Circle(vertexWeight);
        vertexShape.setFill(Color.BEIGE);
        vertexShape.setStroke(Color.DARKBLUE);
        translateXProperty().bind(myVertex.xCoordinatesProperty());
        translateYProperty().bind(myVertex.yCoordinatesProperty());

        getChildren().add(vertexShape);

    }
}
