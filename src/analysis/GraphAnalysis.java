package analysis;

import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.util.Pair;
import model.TaxonNode;
import org.apache.commons.math3.linear.OpenMapRealMatrix;

import java.util.*;
import java.util.stream.Collectors;


public class GraphAnalysis {

    private MyGraph<MyVertex, MyEdge> filteredGraph;
    private HashMap<TaxonNode, Integer> nodeDegrees;


    public GraphAnalysis(MyGraph<MyVertex, MyEdge> completeGraph) {
        filteredGraph = createFilteredGraph(completeGraph);
        nodeDegrees = calcNodeDegrees();
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


    public HashMap<TaxonNode, Integer> calcNodeDegrees() {
        HashMap<TaxonNode, Integer> degreesMap = new HashMap<>();
        for (MyVertex vertex : filteredGraph.getVertices()) {
            degreesMap.put(vertex.getTaxonNode(), filteredGraph.degree(vertex));
        }
        return degreesMap;
    }

    public HashMap<Integer, Double> getDegreeDistribution() {
        HashMap<TaxonNode, Integer> nodeDegrees = calcNodeDegrees();
        Map<Integer, Long> degreeCounts = nodeDegrees.values().stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        HashMap<Integer, Double> degreeDistribution = new HashMap<>();
        for (Map.Entry<Integer, Long> entry : degreeCounts.entrySet()) {
            degreeDistribution.put(entry.getKey(), entry.getValue() / (double) filteredGraph.getVertices().size());
        }
        return degreeDistribution;
    }

    /**
     * Returns a list of Hubs.
     * We define a node as a hub if its degree is at least 1 standard deviation higher than the average degree
     *
     * @return
     */
    public HashMap<TaxonNode, Integer> getHubs() {
        HashMap<TaxonNode, Integer> nodeDegrees = calcNodeDegrees();

        //Calculate Mean: 1/n*sum(nodeDegrees)
        double meanDegree = getMeanDegree();

        //Calculate Standard Deviation: sqrt(1/(n-1)*sum((nodeDegree-meanDegree)^2)
        double standardDeviation = 0;
        for (Integer degree : nodeDegrees.values()) {
            standardDeviation += Math.pow(degree - meanDegree, 2);
        }
        standardDeviation = Math.sqrt(standardDeviation / (nodeDegrees.size() - 1));

        HashMap<TaxonNode, Integer> hubsMap = new HashMap<>();
        for (Map.Entry<TaxonNode, Integer> entry : nodeDegrees.entrySet()) {
            if (entry.getValue() > meanDegree + 1 * standardDeviation)
                hubsMap.put(entry.getKey(), entry.getValue());
        }
        return hubsMap;

    }

    public double getMeanDegree() {
        HashMap<TaxonNode, Integer> nodeDegrees = calcNodeDegrees();

        //Calculate Mean: 1/n*sum(nodeDegrees)
        double degreeSum = 0;

        for (Integer degree : nodeDegrees.values()) {
            degreeSum += degree;
        }
        return degreeSum / nodeDegrees.size();
    }


    //The following methods are an implementation of the algorithm proposed by Clauset et al.
    //(Clauset A, Newman ME, Moore C (2004) Finding community structure in very large networks.
    // Phys Rev E Stat Nonlin Soft Matter Phys 70: 066111.)
    public double findGlobalMaximumModularity() {

        //Store the community of each vertex as an integer
        HashMap<MyVertex, Integer> communityMap = new HashMap<>();


        //Generate a sparse Matrix of communities - since in the beginning, every vertex has its own community,
        //we create it as an |V|x|V| matrix
        OpenMapRealMatrix sparse_Q_ij_Matrix = new OpenMapRealMatrix(filteredGraph.getVertexCount(), filteredGraph.getVertexCount());

        //We also want to store each row of the matrix as a priority queue (which is almost the same thing as a heap)
        //We store all of these queues in a map
        HashMap<Integer, PriorityQueue<Pair<Integer, Double>>> rowsMap = new HashMap<>();

        //Generate array for elements a_i
        double[] a_i_array = new double[filteredGraph.getVertexCount()];

        //Set initial values for the matrix, the priority Queues, the array and the community map as described in Equation (8) and (9)
        initializeModularityAlgorithmParameters(sparse_Q_ij_Matrix, rowsMap, a_i_array, communityMap);


        //We also want another priority queue that contains the largest element of each row
        PriorityQueue<Pair<Integer, Pair<Integer, Double>>> maxQueue = new PriorityQueue<>((o1, o2) -> {
            double value1 = o1.getValue().getValue();
            double value2 = o2.getValue().getValue();
            return Double.compare(value2, value1);
        });
        for (Map.Entry<Integer, PriorityQueue<Pair<Integer, Double>>> entry : rowsMap.entrySet()) {
            maxQueue.add(new Pair<>(entry.getKey(), entry.getValue().peek()));
        }


        //Now the actual algorithm begins
        int numberOfCommunities = filteredGraph.getVertexCount();
        int maxModularity = 0;
        //Repeat until only one community is left
        while (numberOfCommunities > 1) {
            //Retrieve largest entry
            Pair<Integer, Pair<Integer, Double>> largestEntry = maxQueue.poll();
            //Join the communities
            joinCommunities(largestEntry.getKey(), largestEntry.getValue().getKey(), communityMap);
            //Update the sparse matrix, the priority queues and a[i]
            updateParameters(sparse_Q_ij_Matrix, rowsMap, maxQueue, a_i_array, largestEntry.getKey(), largestEntry.getValue().getKey(), communityMap);
            //Update maximal modularity (Remember: This is the value we actually want to calculate!)
            maxModularity += largestEntry.getValue().getValue();
            //Decrement community counter by 1
            numberOfCommunities--;
        }
        //Modularity is now maximized
        return maxModularity;
    }

    /**
     * Joins the two communities i and j by replacing every occurence of Max(i,j) with Min(i,j)
     *
     * @param i
     * @param j
     * @param communityMap
     */
    private void joinCommunities(int i, int j, HashMap<MyVertex, Integer> communityMap) {
        //We always merge the community with the larger id into the community with the smaller id
        communityMap.replaceAll((k, v) -> {
            if (v == Math.max(i, j))
                return Math.min(i, j);
            else return v;
        });
    }

    /**
     * Updates the given parameters based on the two merged communities
     *
     * @param sparse_Q_ij_Matrix
     * @param rowsMap
     * @param maxQueue
     * @param a_i_array
     * @param i
     * @param j
     * @param communityMap
     */
    private void updateParameters(OpenMapRealMatrix sparse_Q_ij_Matrix,
                                  HashMap<Integer, PriorityQueue<Pair<Integer, Double>>> rowsMap,
                                  PriorityQueue<Pair<Integer, Pair<Integer, Double>>> maxQueue,
                                  double[] a_i_array,
                                  int i,
                                  int j,
                                  HashMap<MyVertex, Integer> communityMap) {
        //First, update the matrix
        // 1. Make sure j is smaller than i, swap if necessary
        if (j < i) {
            int tmp = j;
            j = i;
            i = tmp;
        }

        // 2. Update j's row and column and set i's row and column to NaN
        for (int k = 0; k < sparse_Q_ij_Matrix.getColumnDimension(); k++) {
            //Check if k is still a valid community identifier
            if (sparse_Q_ij_Matrix.getEntry(k, 0) == Double.NaN)
                continue;
            //Check if k is connected to both i and j or only to one of them
            double firstSummand, secondSummand;
            if (sparse_Q_ij_Matrix.getEntry(i, k) == 0)
                firstSummand = -2 * a_i_array[i] * a_i_array[k];
            else
                firstSummand = sparse_Q_ij_Matrix.getEntry(i, k);

            if (sparse_Q_ij_Matrix.getEntry(j, k) == 0)
                secondSummand = -2 * a_i_array[j] * a_i_array[k];
            else
                secondSummand = sparse_Q_ij_Matrix.getEntry(j, k);

            //Set Q(j,k) and Q(k,j) to the sum of these two
            sparse_Q_ij_Matrix.setEntry(j, k, firstSummand + secondSummand);
            sparse_Q_ij_Matrix.setEntry(k, j, firstSummand + secondSummand);
            //Set Q(i,k) and Q(k,i) to Double.NaN so that they're ignored from now on
            sparse_Q_ij_Matrix.setEntry(i, k, Double.NaN);
            sparse_Q_ij_Matrix.setEntry(k, i, Double.NaN);
        }


        //Update row queues
        Comparator<Pair<Integer, Double>> myComparator = (o1, o2) -> Double.compare(o2.getValue(), o1.getValue());
        for (int index_i = 0; index_i < sparse_Q_ij_Matrix.getRowDimension(); index_i++) {
            if (sparse_Q_ij_Matrix.getEntry(index_i, 0) == Double.NaN)
                continue;
            PriorityQueue<Pair<Integer, Double>> rowQueue = new PriorityQueue<>(myComparator);
            for (int index_j = 0; index_j < sparse_Q_ij_Matrix.getColumnDimension(); index_j++) {
                if (sparse_Q_ij_Matrix.getEntry(index_i, index_j) == Double.NaN)
                    continue;
                rowQueue.add(new Pair<>(index_j, sparse_Q_ij_Matrix.getEntry(index_i, index_j)));
            }
            rowsMap.put(index_i, rowQueue);

        }

        //Update maxQueue
        maxQueue.clear();
        for (Map.Entry<Integer, PriorityQueue<Pair<Integer, Double>>> entry : rowsMap.entrySet()) {
            maxQueue.add(new Pair<>(entry.getKey(), entry.getValue().peek()));
        }


        //Update a_i_array
        for (int index = 0; index < a_i_array.length; index++) {
            if(sparse_Q_ij_Matrix.getEntry(index,0)!=Double.NaN)
                a_i_array[index] = calcA_i(index, communityMap);
        }

    }


    /**
     * Initializes the values of the given parameters
     *
     * @param sparse_Q_ij_Matrix
     * @param rowsMap
     * @param a_i_array
     * @param communityMap
     */
    private void initializeModularityAlgorithmParameters(OpenMapRealMatrix sparse_Q_ij_Matrix,
                                                         HashMap<Integer, PriorityQueue<Pair<Integer, Double>>> rowsMap,
                                                         double[] a_i_array,
                                                         HashMap<MyVertex, Integer> communityMap) {
        //Both of these measures will be needed below
        double m = filteredGraph.getEdgeCount();
        HashMap<TaxonNode, Integer> nodeDegrees = getNodeDegrees();


        //Initialize community map
        //Initially, every vertex has its own community
        int communityCounter = 0;
        for (MyVertex vertex : filteredGraph.getVertices()) {
            communityMap.put(vertex, communityCounter);
            communityCounter++;
        }


        //Define a comparator for the priority queues
        Comparator<Pair<Integer, Double>> myComparator = (o1, o2) -> Double.compare(o2.getValue(), o1.getValue());

        //In these loops, the initial values for the array of a_i, the row-PriorityQueues and the sparse matrix are set
        for (Map.Entry<MyVertex, Integer> entry_i : communityMap.entrySet()) {
            a_i_array[entry_i.getValue()] = nodeDegrees.get(entry_i.getKey().getTaxonNode()) / (2 * m);
            PriorityQueue<Pair<Integer, Double>> rowQueue = new PriorityQueue<>(myComparator);
            for (Map.Entry<MyVertex, Integer> entry_j : communityMap.entrySet()) {
                double q_value;
                if (filteredGraph.findEdge(entry_i.getKey(), entry_j.getKey()) != null) {
                    q_value = 1 / (2 * m) - (nodeDegrees.get(entry_i.getKey().getTaxonNode()) * nodeDegrees.get(entry_j.getKey().getTaxonNode()))
                            / Math.pow(2 * m, 2);
                } else
                    q_value = 0;
                sparse_Q_ij_Matrix.setEntry(entry_i.getValue(), entry_j.getValue(), q_value);
                rowQueue.add(new Pair<>(entry_j.getValue(), q_value));

            }
            rowsMap.put(entry_i.getValue(), rowQueue);
        }

    }

    //Given communities i and j, calculates the fraction of edges that join vertices between these two communities
//    private double calcE_ij(int i, int j, HashMap<MyVertex, Integer> communityMap) {
//        double m = filteredGraph.getEdgeCount();
//
//        double edgesSum = 0;
//        for (MyVertex v : filteredGraph.getVertices()) {
//            for (MyVertex w : filteredGraph.getVertices()) {
//                if (filteredGraph.findEdge(v, w) != null && communityMap.get(v) == i && communityMap.get(w) == j)
//                    edgesSum++;
//            }
//        }
//        return 1 / (2 * m) * edgesSum;
//    }

    //Calculates the fraction of ends of edges that are attached to vertices in community i
    private double calcA_i(int i, HashMap<MyVertex, Integer> communityMap) {
        double m = filteredGraph.getEdgeCount();
        HashMap<TaxonNode, Integer> nodeDegrees = getNodeDegrees();

        double endsSum = 0;
        for (MyVertex v : filteredGraph.getVertices()) {
            if (communityMap.get(v) == i)
                endsSum += nodeDegrees.get(v.getTaxonNode());
        }
        return 1 / (2 * m) * endsSum;
    }


    //Getters
    public MyGraph<MyVertex, MyEdge> getFilteredGraph() {
        return filteredGraph;
    }

    public HashMap<TaxonNode, Integer> getNodeDegrees() {
        return nodeDegrees;
    }

    //Main for debugging
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
        HashMap<TaxonNode, Integer> nodeDegrees = analysis.calcNodeDegrees();
        for (Map.Entry<TaxonNode, Integer> taxonNodeIntegerEntry : nodeDegrees.entrySet()) {
            System.out.println(taxonNodeIntegerEntry.getKey().getTaxonId() + " --> " + taxonNodeIntegerEntry.getValue());
        }


        System.out.println("Degree distribution:");
        Map<Integer, Double> degreeDistribution = analysis.getDegreeDistribution();
        for (Map.Entry<Integer, Double> entry : degreeDistribution.entrySet()) {
            System.out.println(entry.getKey() + " --> " + entry.getValue());
        }

        System.out.println(analysis.findGlobalMaximumModularity());


    }
}
