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
    private BooleanProperty correlationAndPValueInRange;
    private BooleanProperty frequencyInRange;

    public MyEdge(MyVertex source, MyVertex target) {
        this.source = source;
        this.target = target;
        attributesMap = new HashMap<>();
        source.getEdgesList().add(this);
        target.getEdgesList().add(this);
        setupListeners();
    }

    public MyEdge(MyVertex source, MyVertex target, double weight) {
        this.source = source;
        this.target = target;
        attributesMap = new HashMap<>();
        this.weight = weight;
        setupListeners();
    }

    private void setupListeners() {
        correlationAndPValueInRange = new SimpleBooleanProperty(true);
        frequencyInRange = new SimpleBooleanProperty(true);
        //Add listeners for the two range properties - if both are false, set hidden to true
        correlationAndPValueInRange.addListener(observable -> {
            if (correlationAndPValueInRange.get() && frequencyInRange.get()) {
                showEdge();
            } else {
                hideEdge();
            }
        });

        frequencyInRange.addListener(observable -> {
            if (correlationAndPValueInRange.get() && frequencyInRange.get()) {
                showEdge();
            } else {
                hideEdge();
            }
        });
    }

    public void hideEdge() {
        if (!isHidden()) {
            source.setNumberofVisibleEdges(source.getNumberofVisibleEdges() - 1);
            target.setNumberofVisibleEdges(target.getNumberofVisibleEdges() - 1);
        }
        isHidden.set(true);
    }

    public void showEdge() {
        if (isHidden()) {
            source.setNumberofVisibleEdges(source.getNumberofVisibleEdges() + 1);
            target.setNumberofVisibleEdges(target.getNumberofVisibleEdges() + 1);
        }
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

    public double getPValue() {
        return pValue;
    }

    public void setPValue(double pValue) {
        this.pValue = pValue;
    }

    public boolean isHidden() {
        return isHidden.get();
    }

    public BooleanProperty isHiddenProperty() {
        return isHidden;
    }

    public boolean isCorrelationAndPValueInRange() {
        return correlationAndPValueInRange.get();
    }

    public BooleanProperty correlationAndPValueInRangeProperty() {
        return correlationAndPValueInRange;
    }

    public void setCorrelationAndPValueInRange(boolean correlationAndPValueInRange) {
        this.correlationAndPValueInRange.set(correlationAndPValueInRange);
    }

    public boolean isFrequencyInRange() {
        return frequencyInRange.get();
    }

    public BooleanProperty frequencyInRangeProperty() {
        return frequencyInRange;
    }

    public void setFrequencyInRange(boolean frequencyInRange) {
        this.frequencyInRange.set(frequencyInRange);
    }
}
