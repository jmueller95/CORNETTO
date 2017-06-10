package graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * Created by julian on 10.06.17.
 */
public class MyEdge extends DefaultEdge {
    private MyVertex source;
    private MyVertex target;
    private boolean isHidden = false;
    //TODO: Attributes

    public MyEdge(MyVertex source, MyVertex target) {
        this.source = source;
        this.target = target;
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
}
