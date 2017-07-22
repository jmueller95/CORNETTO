package graph;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.HashMap;

/**
 * Created by julian on 10.06.17.
 */
public class MyEdge {

    private MyVertex source;
    private MyVertex target;
    private BooleanProperty isHidden = new SimpleBooleanProperty(false);
    private double weight;
    private HashMap<String, Object> attributesMap;
    private double correlation;
    private double pValue;

    public MyEdge(MyVertex source, MyVertex target) {
        this.source = source;
        this.target = target;
        attributesMap = new HashMap<>();
    }

    public MyEdge(MyVertex source, MyVertex target, double weight) {
        this.source = source;
        this.target = target;
        attributesMap = new HashMap<>();
        this.weight = weight;
    }


    public void hideEdge() {
        isHidden.set(true);
    }

    public void showEdge() {
        isHidden.set(false);
    }

    public MyVertex getSource() {
        return source;
    }

    public MyVertex getTarget() {
        return target;
    }

    public double getWeight() {
        return weight;
    }

    public HashMap<String, Object> getAttributesMap() {
        return attributesMap;
    }

    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }

    public double getCorrelation() {
        return correlation;
    }

    public void setPValue(double pValue) {
        this.pValue = pValue;
    }

    public boolean isHidden(){
        return isHidden.get();
    }

    public BooleanProperty isHiddenProperty() {
        return isHidden;
    }
}
