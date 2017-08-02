package view;

import graph.MyEdge;
import graph.MyVertex;
import javafx.beans.property.*;
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
import main.GlobalConstants;

/**
 * <h1>The class is our implementation of the edges of the graph</h1>
 * <p>
 * There are methods for setting the colors of the edges.
 *
 * </p>
 *
 */
public class MyEdgeView extends Group {

    private MyEdge myEdge;
    private Line edgeShape;

    // Display Properties
    ObjectProperty<Palette> colourProperty;
    StringProperty colourAttribute;

    /**
     * Constructor for EdgeView, takes reference to MyEdge object in the main Graph
     * @param myEdge
     */
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
        colourProperty = new SimpleObjectProperty<>(Palette.RdBu);
        colourAttribute = new SimpleStringProperty("correlation");
        colourAttribute.addListener((e, o, n) -> refreshColour());
        colourProperty.addListener((e, o, n) -> refreshColour());

        refreshColour();

        getChildren().add(edgeShape);
        Tooltip tooltip = new Tooltip(myEdge.getSource().getTaxonName() + " --- " + myEdge.getTarget().getTaxonName()
                + "\nCorrelation: " + String.format("%.3f", myEdge.getCorrelation())
                + "\np-Value: " + String.format("%.3f", myEdge.getPValue()));
        tooltip.setFont(Font.font(14));
        Tooltip.install(this, tooltip);

    }

    /**
     * Refreshes NodeColour based on the Palette set in colourProperty and the Attribute value of MyVertex
     * defined in colourAttribute Porperty
     */
    private void refreshColour() {

        switch (colourAttribute.get()) {

            // Fixed colours --> set to predefined standard
            case "pValue":
                edgeShape.setStroke(MyColours.interpolate(colourProperty.get(), myEdge.getPValue()));
                break;

            case "correlation":
                double t = (myEdge.getCorrelation() + 1)/2;
                edgeShape.setStroke(MyColours.interpolate(colourProperty.get(), t));
                break;

        }
    }

    /**
     * Returns Width Property
     * @return
     */
    public DoubleProperty getWidthProperty() {
        return edgeShape.strokeWidthProperty();
    }

    /**
     * Returns MyEdge object
     * @return
     */
    public MyEdge getMyEdge() {
        return myEdge;
    }
}
