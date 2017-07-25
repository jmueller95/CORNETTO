package view;

import graph.MyEdge;
import graph.MyVertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Line;

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
        //lineThickness = myEdge.getWeight();
        edgeShape = new Line();
        edgeShape.startXProperty().bind((myEdge.getSource()).xCoordinatesProperty());
        edgeShape.startYProperty().bind((myEdge.getSource()).yCoordinatesProperty());
        edgeShape.endXProperty().bind((myEdge.getTarget()).xCoordinatesProperty());
        edgeShape.endYProperty().bind((myEdge.getTarget()).yCoordinatesProperty());

        //Bind to hidden property of myEdge object
        visibleProperty().bind(myEdge.isHiddenProperty().not());

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
        if (myEdge.getCorrelation() >= 0) edgeShape.setStroke(Color.DARKGREEN);
        else edgeShape.setStroke(Color.DARKRED);
    }

    public DoubleProperty getWidthProperty() {
        return edgeShape.strokeWidthProperty();
    }


}
