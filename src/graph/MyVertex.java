package graph;

import javafx.beans.property.*;
import model.TaxonNode;

import java.util.HashMap;

/**
 * Created by julian on 10.06.17.
 * //TODO: A vertex should have a list of edges, and if all of them are hidden, it should hide as well!
 */
public class MyVertex {
    private TaxonNode taxonNode;
    private boolean isHidden = false;

    private DoubleProperty xCoordinates;
    private DoubleProperty yCoordinates;
    private Property<String> vertexLabel;
    private HashMap<String, Object> attributesMap;

    public MyVertex(Object content) {
        xCoordinates = new SimpleDoubleProperty(0);
        yCoordinates = new SimpleDoubleProperty(0);
        vertexLabel = new SimpleStringProperty("initName");
        attributesMap = new HashMap<>();

    }


    public double getxCoordinates() {
        return xCoordinates.get();
    }

    public DoubleProperty xCoordinatesProperty() {
        return xCoordinates;
    }

    public DoubleProperty yCoordinatesProperty() {
        return yCoordinates;
    }

    public void hideVertex() {
        this.isHidden = true;
    }

    public void showVertex() {
        this.isHidden = false;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public HashMap<String, Object> getAttributesMap() {
        return attributesMap;
    }


}
