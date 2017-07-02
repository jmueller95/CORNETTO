package graph;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;

/**
 * Created by julian on 10.06.17.
 */
public class MyEdge extends DefaultWeightedEdge {
    private MyVertex source;
    private MyVertex target;
    private boolean isHidden = false;
    private double weight;
    private HashMap<String, Object> attributesMap;

    public MyEdge(MyVertex source, MyVertex target) {
        this.source = source;
        this.target = target;
        attributesMap = new HashMap<>();
    }

    public MyEdge() {
        super();
    }

    public void hideEdge(){
        this.isHidden = true;
    }

    public void showEdge(){
        this.isHidden = false;
    }

    @Override
    public MyVertex getSource() {
        return source;
    }

    @Override
    public MyVertex getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "(" + source.getContent() + " : " + target.getContent() + ")";
    }

    @Override
    public double getWeight() {
        return super.getWeight();
    }

    public HashMap<String, Object> getAttributesMap() {
        return attributesMap;
    }

}
