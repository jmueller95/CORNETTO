package analysis;

import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.math3.geometry.spherical.twod.Edge;
import java.util.*;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;

/**
 * Created by NantiaL on 01.08.2017.
 */
public class KMeansClustering {

    /**
     * computes the clustering coefficient and gets as parameter a graph whose clustering coefficients are to be calculated
     * @param graph
     * @return
     */
    public static  Map<MyVertex, Double> ClusteringCoefficient(MyGraph<MyVertex,MyEdge> graph)
    {
        Map<MyVertex,Double> coefficients = new HashMap<MyVertex,Double>();

        for (MyVertex vertex : graph.getVertices())
        {
            int neighborCount = graph.getNeighborCount(vertex);
            if (neighborCount < 2)
                coefficients.put(vertex, new Double(0));
            else
            {
                // how many of vertices' neighbors are connected to each other?
                ArrayList<MyVertex> neighbors = new ArrayList<MyVertex>(graph.getNeighbors(vertex));
                double edge_count = 0;
                for (int i = 0; i < neighborCount; i++)
                {
                    MyVertex getneighbors = neighbors.get(i);
                    for (int j = i+1; j < neighborCount; j++ )
                    {
                        MyVertex x = neighbors.get(j);
                        edge_count += graph.isNeighbor(getneighbors, x) ? 1 : 0;
                    }
                }
                double possible_edges = (neighborCount * (neighborCount - 1))/2.0;
                coefficients.put(vertex, new Double(edge_count / possible_edges));
            }
        }

        return coefficients;
    }



    }

