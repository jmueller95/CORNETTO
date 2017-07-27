package view;

import graph.MyVertex;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import model.AnalysisData;

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
    private Tooltip tooltip;

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

        visibleProperty().bind(myVertex.isHiddenProperty().not());


        tooltip = new Tooltip(myVertex.getTaxonNode().getName() + "\nID: " + myVertex.getTaxonNode().getTaxonId()
        + "\nRelative Frequency: " + String.format("%.3f", AnalysisData.getMaximumRelativeFrequencies().get(myVertex.getTaxonNode())));
        tooltip.setFont(Font.font(14));
        Tooltip.install(this,tooltip);
        getChildren().add(vertexShape);

        addLabel();

    }

    private void addLabel() {
        vertexLabel = new Label(myVertex.getTaxonName());
        vertexLabel.translateXProperty().bind(vertexShape.translateXProperty());
        vertexLabel.translateYProperty().bind(vertexShape.translateYProperty().add(getRadiusProperty()));
        vertexLabel.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 12));
        getChildren().add(vertexLabel);
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
