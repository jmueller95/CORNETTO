package graph;

import java.util.HashMap;

/**
 * Created by julian on 10.06.17.
 */
public class MyEdge {

    private MyVertex source;
    private MyVertex target;
    private boolean isHidden = false;
    private double weight;
    private HashMap<String, Object> attributesMap;
    private double correlation;
    private double pValue;

    public MyEdge(MyVertex source, MyVertex target) {
        this.source = source;
        this.target = target;
        attributesMap = new HashMap<>();
    }


    public void hideEdge() {
        this.isHidden = true;
    }

    public void showEdge() {
        this.isHidden = false;
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

    public void setPValue(double pValue) {
        this.pValue = pValue;
    }
}
