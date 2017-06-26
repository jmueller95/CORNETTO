package view;

import graph.MyEdge;
import graph.MyVertex;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Line;
import org.jgraph.graph.Edge;

/**
 * Created by caspar on 19.06.17.
 */
public class MyEdgeView extends Group {

    MyEdge myEdge;
    double lineThickness = 10;
    Line edgeShape;
    Label edgelabel;

    public MyEdgeView(MyEdge myEdge) {
        this.myEdge = myEdge;
        edgeShape = new Line();
        edgeShape.startXProperty().bind(((MyVertex)myEdge.getSource()).xCoordinatesProperty());
        edgeShape.startYProperty().bind(((MyVertex)myEdge.getSource()).yCoordinatesProperty());
        edgeShape.endXProperty().bind(((MyVertex)myEdge.getTarget()).xCoordinatesProperty());
        edgeShape.endYProperty().bind(((MyVertex)myEdge.getTarget()).yCoordinatesProperty());


        edgeShape.setStrokeWidth(lineThickness);
        setColor();

        getChildren().add(edgeShape);
        addLabel();

    }

    public void addLabel() {
        // TODO
    }

    public void setColor() {
        // set interactively and/or based on attributes
        edgeShape.setStroke(Color.DARKBLUE);
    }


}
