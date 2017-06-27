package graph;

import javafx.beans.property.*;

import java.util.HashMap;

/**
 * Created by julian on 10.06.17.
 */
public class MyVertex {
    private Object content; //TODO: Change this e.g. to TaxonNode
    private boolean isHidden = false;

    private DoubleProperty xCoordinates;
    private DoubleProperty yCoordinates;
    private Property<String> vertexLabel;
    private HashMap<String, Object> attributesMap;

    public MyVertex(Object content) {
        this.content = content;
        xCoordinates = new SimpleDoubleProperty(0);
        yCoordinates = new SimpleDoubleProperty(0);
        vertexLabel = new SimpleStringProperty("initName");

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

    public Object getContent() {
        return content;
    }

    public HashMap<String, Object> getAttributesMap() {
        return attributesMap;
    }


}
