package graph;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by julian on 10.06.17.
 */
public class MyVertex {
    private Object content; //TODO: Change this e.g. to TaxonNode
    private boolean isHidden = false;

    private DoubleProperty xCoordinates;
    private DoubleProperty yCoordinates;
    private Property<String> vertexLabel;

    public MyVertex(Object content) {
        this.content = content;
        // Assign random coordinates //TODO: change this
        xCoordinates = new SimpleDoubleProperty(Math.random()*500);
        yCoordinates = new SimpleDoubleProperty(Math.random()*500);
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
}
