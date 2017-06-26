package graph;

import com.jgraph.layout.JGraphFacade;
import org.jgraph.JGraph;

import java.util.Collection;

/**
 * Created by caspar on 27.06.17.
 */
public class MyGraphFacade extends JGraphFacade {


    public MyGraphFacade(JGraph jGraph) {
        super(jGraph);

    }

    public double[][] getAllLocations() {
        return super.getLocations(super.getCells(super.getAll(), false, false).toArray());
    }
}
