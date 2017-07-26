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
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

/**
 * Created by caspar on 19.06.17.
 */
public class MyEdgeView extends Group {

    MyEdge myEdge;
    double lineThickness = 10;
    Line edgeShape;
    Label edgelabel;
    Tooltip tooltip;

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
        tooltip = new Tooltip(myEdge.getSource().getTaxonName() + " --- " + myEdge.getTarget().getTaxonName()
                + "\nCorrelation: " + String.format("%.3f", myEdge.getCorrelation())
                + "\np-Value: " + String.format("%.3f", myEdge.getPValue()));
        tooltip.setFont(Font.font(14));
        Tooltip.install(this, tooltip);

        addLabel();


    }

    public void addLabel() {

    }

    public void setColor() {
        // set interactively and/or based on attributes
        if (myEdge.getCorrelation() >= 0) edgeShape.setStroke(Color.DARKGREEN);
        else edgeShape.setStroke(Color.DARKRED);
    }

    public DoubleProperty getWidthProperty() {
        return edgeShape.strokeWidthProperty();
    }

    public Label getEdgelabel() {
        return edgelabel;
    }
}
