package graph;

import javafx.beans.property.*;
import model.TaxonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by julian on 10.06.17.
 */
public class MyVertex {
    private TaxonNode taxonNode;
    private BooleanProperty isHidden = new SimpleBooleanProperty(false);
    private BooleanProperty isSelected = new SimpleBooleanProperty(false);
    private BooleanProperty isHub = new SimpleBooleanProperty(false);

    private DoubleProperty xCoordinates;
    private DoubleProperty yCoordinates;
    private Property<String> vertexLabel;
    private IntegerProperty numberofVisibleEdges;
    private List<MyEdge> edgesList;
    private HashMap<String, Object> attributesMap;

    /**
     * defines a vertex
     * @param taxonNode
     * @param numberOfEdges
     */
    public MyVertex(TaxonNode taxonNode, int numberOfEdges) {
        this.taxonNode = taxonNode;
        xCoordinates = new SimpleDoubleProperty(0);
        yCoordinates = new SimpleDoubleProperty(0);
        vertexLabel = new SimpleStringProperty("initName");
        numberofVisibleEdges = new SimpleIntegerProperty(numberOfEdges);
        attributesMap = new HashMap<>();

        edgesList = new ArrayList<>();
        //Vertex should be hidden if number of visible edges reaches 0
        numberofVisibleEdges.addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0) {
                hideVertex();
            } else {
                showVertex();
            }
        });

        // Add TaxonName of Parent to the attributesMap
        attributesMap.put("parentName", taxonNode.getParentNode().getName());
    }


    public double getXCoordinates() {
        return xCoordinates.get();
    }

    public double getYCoordinates() {
        return yCoordinates.get();
    }

    public DoubleProperty xCoordinatesProperty() {
        return xCoordinates;
    }

    public DoubleProperty yCoordinatesProperty() {
        return yCoordinates;
    }

    public BooleanProperty isSelectedProperty() {
        return isSelected;
    }

    public void hideVertex() {
        isHidden.setValue(true);
    }

    public void showVertex() {
        isHidden.set(false);
    }

    public boolean isHidden() {
        return isHidden.get();
    }

    public BooleanProperty isHiddenProperty() {
        return isHidden;
    }

    public HashMap<String, Object> getAttributesMap() {
        return attributesMap;
    }

    public TaxonNode getTaxonNode() {
        return taxonNode;
    }

    public String getTaxonName() {
        return taxonNode.getName();
    }

    public int getNumberofVisibleEdges() {
        return numberofVisibleEdges.get();
    }

    public IntegerProperty numberofVisibleEdgesProperty() {
        return numberofVisibleEdges;
    }

    public void setNumberofVisibleEdges(int numberofVisibleEdges) {
        this.numberofVisibleEdges.set(numberofVisibleEdges);
    }

    public List<MyEdge> getEdgesList() {
        return edgesList;
    }

    public boolean isHub() {
        return isHub.get();
    }

    public BooleanProperty isHubProperty() {
        return isHub;
    }

    public void setIsHub(boolean isHub) {
        this.isHub.set(isHub);
    }
}
