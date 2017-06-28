package graph;

import com.jgraph.layout.organic.JGraphOrganicLayout;

import java.awt.geom.Point2D;

/**
 * Created by caspar on 28.06.17.
 */
public class MyOrganicLayout extends JGraphOrganicLayout {



    @Override
    protected double getEdgeLength(int i) {
        if (isOptimizeEdgeLength) {
            double edgeLength = Point2D.distance(v[e[i].getSource()].getX(),
                    v[e[i].getSource()].getY(), v[e[i].getTarget()].getX(), v[e[i].getTarget()].getY());
            return (edgeLengthCostFactor * edgeLength * edgeLength);
        } else {
            return 0.0;
        }
    }
}
