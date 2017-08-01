package view;

import graph.MyVertex;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import model.AnalysisData;

/**
 * Creates a Node in the View area for each Node
 * Handles Selection, Radius change, and Colour Scheme settings
 * Created by caspar on 19.06.17.
 */
public class MyVertexView extends Group {

    // Style constants
    private static final Double NODE_RADIUS = 10.0;
    private static final Color FILL = Color.BEIGE;
    private static final Color STROKE = Color.DARKBLUE;
    private static final Color SELECT = Color.DARKORANGE;
    private static final Color HUBFILL = Color.color(1.0f, 0.4f, 0.4f);


    // Basic variables
    MyVertex myVertex;
    Circle vertexShape;
    Label vertexLabel;
    private Tooltip tooltip;

    // Display Properties
    ObjectProperty<Palette> colourProperty;
    StringProperty colourAttribute;

    /**
     * Constructor for VertexView, takes a MyVertex object as reference
     * @param myVertex
     */
    public MyVertexView(MyVertex myVertex) {
        this.myVertex = myVertex;

        // Initialize stuff
        vertexShape = new Circle(NODE_RADIUS);
        vertexShape.setStroke(STROKE);

        colourProperty = new SimpleObjectProperty<>(Palette.BrBG);
        colourAttribute = new SimpleStringProperty("fix");

        // Bind Coordinates to MyVertex
        translateXProperty().bindBidirectional(myVertex.xCoordinatesProperty());
        translateYProperty().bindBidirectional(myVertex.yCoordinatesProperty());

        // Bind actions to the Properties
        visibleProperty().bind(myVertex.isHiddenProperty().not());
        colourAttribute.addListener((e, o, n) -> refreshColour());
        colourProperty.addListener((e, o, n) -> refreshColour());

        // Add Layout elements
        refreshColour();
        addSelectionMarker();
        addToolTip();
        addLabel();

        getChildren().add(vertexShape);
        //Listen to the isHub-Property of the MyVertex object, make vertexShape thicker if it's a hub
        myVertex.isHubProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                vertexShape.setStrokeWidth(vertexShape.getStrokeWidth() * 3);
                vertexShape.setStroke(Color.BLACK);
                vertexShape.setFill(HUBFILL);
            } else {
                vertexShape.setStrokeWidth(vertexShape.getStrokeWidth() / 3);
                vertexShape.setStroke(Color.DARKBLUE);
                vertexShape.setFill(HUBFILL);
            }
        });

    }

    /**
     * Refresehs NodeColour based on the Palette set in colourProperty and the Attribute value of MyVertex
     * defined in colourAttribute Porperty
     */
    private void refreshColour() {

        switch (colourAttribute.get()) {

            // Fixed colours --> set to predefined standard
            case "fix":
                vertexShape.setFill(FILL);
                break;

            case "sample":
                //TODO
                vertexShape.setFill(Color.CADETBLUE);
                break;

            case "alpha":
                vertexShape.setFill(MyColours.interpolate(colourProperty.get(), (double)myVertex.getAttributesMap().get("alpha")));
                break;

            case "modularity":
                double t = (double)myVertex.getAttributesMap().get("modularity");
                vertexShape.setFill(MyColours.interpolate(colourProperty.get(), (t+1)/2));
        }
    }

    /**
     * Adds Label Display to every Node containing the Taxon ID
     */
    private void addLabel() {
        vertexLabel = new Label(myVertex.getTaxonName());
        vertexLabel.translateXProperty().bind(vertexShape.translateXProperty());
        vertexLabel.translateYProperty().bind(vertexShape.translateYProperty().add(getRadiusProperty()));
        vertexLabel.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 12));
        getChildren().add(vertexLabel);
    }

    /**
     * Adds ToolTip from ToolTop library on Mouse Hover
     */
    private void addToolTip() {
        tooltip = new Tooltip(myVertex.getTaxonNode().getName() + "\nID: " + myVertex.getTaxonNode().getTaxonId()
                + "\nRelative Frequency: " + String.format("%.3f", AnalysisData.getMaximumRelativeFrequencies().get(myVertex.getTaxonNode())));
        tooltip.setFont(Font.font(14));
        Tooltip.install(this,tooltip);
    }

    /**
     * Add mouse drag event to move nodeViews inside the parent group manually
     * Bidirectional bind also updates coordinates in node class
     */
    public void addSelectionMarker() {
        myVertex.isSelectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                vertexShape.setFill(SELECT);
                System.out.println("node selected");
            } else {
                if (myVertex.isHub())
                    vertexShape.setFill(HUBFILL);
                else
                    vertexShape.setFill(FILL);
                System.out.println("node unselected");

            }
        }));
    }

    public DoubleProperty getRadiusProperty() {
        return vertexShape.radiusProperty();
    }

    public MyVertex getMyVertex() {
        return myVertex;
    }

    public void setRadius(double r) {
        vertexShape.setRadius(r);
    }

    public Label getVertexLabel() {
        return vertexLabel;
    }
}
