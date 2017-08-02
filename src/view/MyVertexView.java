package view;

import graph.MyVertex;
import javafx.beans.property.*;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import main.GlobalConstants;
import model.AnalysisData;


/**
 * <h1>The class implements methods for showing the vertices</h1>
 * <p>
 * Depending on if the vertices are selected or not they may be calculated differently
 * The vertices can also be dragged and moved. Furthermore the class handles changing the radiuses and generally
 * color scheme settings
 * </p>
 *
 * @see MyColours
 * @see Palette
 */

public class MyVertexView extends Group {

    // Style constants
    private static final Double NODE_RADIUS = 10.0;
    private static final Color FILL = Color.BEIGE;
    private static final Color STROKE =  Color.color(0.2, 0.2, 0.2);
    private static final Color SELECT = Color.BLUE;
    private static final Color HUBSTROKE = Color.color(0.9f, 0.3f, 0.2f);

    // Basic variables
    MyVertex myVertex;
    Circle vertexShape;
    Circle selectionShape;
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
        selectionShape = new Circle(NODE_RADIUS+4);
        selectionShape.setStroke(STROKE);
        selectionShape.setFill(SELECT);
        selectionShape.setVisible(false);
        selectionShape.radiusProperty().bind(vertexShape.radiusProperty().add(4));

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

        getChildren().addAll(selectionShape, vertexShape);
        //Listen to the isHub-Property of the MyVertex object, make vertexShape thicker if it's a hub
        myVertex.isHubProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                vertexShape.setStrokeWidth(vertexShape.getStrokeWidth() * 4);
                vertexShape.getStrokeDashArray().addAll(3.0,7.0,3.0,7.0);
                vertexShape.setStroke(HUBSTROKE);
            } else {
                vertexShape.setStrokeWidth(vertexShape.getStrokeWidth() / 4);
                vertexShape.getStrokeDashArray().clear();
                vertexShape.setStroke(STROKE);
                vertexShape.setFill(FILL);
            }
        });

    }

    /**
     * Refreshes NodeColour based on the Palette set in colourProperty and the Attribute value of MyVertex
     * defined in colourAttribute Porperty
     */
    private void refreshColour() {

        switch (colourAttribute.get()) {

            // Fixed colours --> set to predefined standard
            case "fix":
                vertexShape.setFill(FILL);
                break;

            case "parentName":
                //
                long seed = stringToSeed((String)myVertex.getAttributesMap().get("parentName"));
                GlobalConstants.globalRandomInstance.setSeed(seed);
                double t1 = GlobalConstants.globalRandomInstance.nextDouble();
                vertexShape.setFill(MyColours.interpolate(colourProperty.get(), t1));
                break;

            case "alpha":
                vertexShape.setFill(MyColours.interpolate(colourProperty.get(), (double)myVertex.getAttributesMap().get("alpha")));
                break;

            case "frequency":
                double t2 = (double)myVertex.getAttributesMap().get("frequency");
                vertexShape.setFill(MyColours.interpolate(colourProperty.get(), t2));
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
        selectionShape.visibleProperty().bind(myVertex.isSelectedProperty());
    }

    /**
     * Helper function to convert String into long for Random seed
     * @param s String used as input
     * @return long
     */
    private long stringToSeed(String s) {
        if (s == null) {
            return 0;
        }
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L*hash + c;
        }
        return hash;
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
