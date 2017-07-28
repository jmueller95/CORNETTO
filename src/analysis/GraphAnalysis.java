package analysis;

import com.google.common.collect.Multiset;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import model.TaxonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class GraphAnalysis {

    private MyGraph<MyVertex, MyEdge> filteredGraph;

    public GraphAnalysis(MyGraph<MyVertex, MyEdge> completeGraph) {
        filteredGraph = createFilteredGraph(completeGraph);
    }

    /**
     * @param completeGraph
     */
    public MyGraph<MyVertex, MyEdge> createFilteredGraph(MyGraph<MyVertex, MyEdge> completeGraph) {
        MyGraph<MyVertex, MyEdge> filteredGraph = new MyGraph<>();
        for (MyVertex myVertex : completeGraph.getVertices()) {
            if (!myVertex.isHidden())
                filteredGraph.addVertex(myVertex);
        }

        for (MyEdge myEdge : completeGraph.getEdges()) {
            if (!myEdge.isHidden())
                filteredGraph.addEdge(myEdge, myEdge.getSource(), myEdge.getTarget());
        }

        return filteredGraph;
    }


    public HashMap<TaxonNode, Integer> getNodeDegrees() {
        HashMap<TaxonNode, Integer> degreesMap = new HashMap<>();
        for (MyVertex vertex : filteredGraph.getVertices()) {
            degreesMap.put(vertex.getTaxonNode(), filteredGraph.degree(vertex));
        }
        return degreesMap;
    }

    public HashMap<Integer, Double> getDegreeDistribution() {
        HashMap<TaxonNode, Integer> nodeDegrees = getNodeDegrees();
        Map<Integer, Long> degreeCounts = nodeDegrees.values().stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        HashMap<Integer, Double> degreeDistribution = new HashMap<>();
        for (Map.Entry<Integer, Long> entry : degreeCounts.entrySet()) {
            degreeDistribution.put(entry.getKey(), entry.getValue() / (double) filteredGraph.getVertices().size());
        }
        return degreeDistribution;
    }

    public static void main(String[] args) {
        MyGraph<MyVertex, MyEdge> graph = new MyGraph<>();
        MyVertex v1 = new MyVertex(new TaxonNode(1, null, 0), 0);
        MyVertex v2 = new MyVertex(new TaxonNode(2, null, 0), 0);
        MyVertex v3 = new MyVertex(new TaxonNode(3, null, 0), 0);
        MyVertex v4 = new MyVertex(new TaxonNode(4, null, 0), 0);
        MyEdge e1 = new MyEdge(v1, v2);
        MyEdge e2 = new MyEdge(v1, v3);
        //Degrees should be: v1=2, v2=1, v3=1, v4=0
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);
        graph.addEdge(e1, v1, v2);
        graph.addEdge(e2, v1, v3);

        for (MyVertex vertex : graph.getVertices()) {
            vertex.showVertex();
        }

        for (MyEdge myEdge : graph.getEdges()) {
            myEdge.showEdge();
        }

        GraphAnalysis analysis = new GraphAnalysis(graph);


        System.out.println("Graph structure:");
        System.out.println(analysis.filteredGraph.getVertices().size() + " Vertices, " + analysis.filteredGraph.getEdges().size() + " Edges");


        System.out.println("Node Degrees:");
        HashMap<TaxonNode, Integer> nodeDegrees = analysis.getNodeDegrees();
        for (Map.Entry<TaxonNode, Integer> taxonNodeIntegerEntry : nodeDegrees.entrySet()) {
            System.out.println(taxonNodeIntegerEntry.getKey().getTaxonId() + " --> " + taxonNodeIntegerEntry.getValue());
        }


        System.out.println("Degree distribution:");
        Map<Integer, Double> degreeDistribution = analysis.getDegreeDistribution();
        for (Map.Entry<Integer, Double> entry : degreeDistribution.entrySet()) {
            System.out.println(entry.getKey() + " --> " + entry.getValue());
        }


    }
}
