package view;

import com.google.common.base.Function;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.lang.Thread;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by caspar on 16.07.17.
 */
public class SpringAnimationService extends Service<String> {

    private SpringLayout2 springLayout;
    private Function<MyEdge, Integer> myEdgeLengthFunction;
    private MyGraph<MyVertex, MyEdge> graph;
    private MyGraphView graphView;
    private int width = 1000;
    private int height =  800;
    private int LENGTH_FACTOR = 300;

    private int max_length = 300;
    private int min_length = 50;

    /**
     * Constructor for the AnimationService
     * @param graph GraphModel which is the basis of the Layout
     * @param graphView GraphView to get extent of the view.
     */
    public SpringAnimationService(MyGraph graph, MyGraphView graphView) {
        this.graph = graph;
        this.graphView = graphView;
    }

    @Override
    public boolean cancel() {
        System.out.println("Paused/Cancelled service");
        return super.cancel();
    }


    public void resume() {
        super.reset();
        super.start();
        System.out.println("Restarted service");
        for (MyVertex vertex: graph.getVertices()) {
            springLayout.setLocation(vertex, vertex.getXCoordinates(), vertex.getYCoordinates());
        }

    }

    @Override
    public void start() {
        super.start();
    }

    /**
     * Initializes Service before use. Needs graph as argument.
     * @return
     */
    @Override
    protected Task<String> createTask() {

        myEdgeLengthFunction = getMyEdgeLengthFunction();
        springLayout = new SpringLayout2(graph, myEdgeLengthFunction);
        springLayout.setSize(new Dimension(width, height));

        return new Task<String>() {

            //Timer timer = new Timer();
            /*TimerTask drawFrame = new TimerTask() {
                @Override
                public void run() {

                    width = (int)graphView.getBoundsInParent().getWidth();
                    height = (int)graphView.getBoundsInParent().getHeight();

                    // Calculate next iteration and move nodes
                    springLayout.step();
                    graph.getVertices().forEach((node) -> {
                        node.xCoordinatesProperty().setValue(springLayout.getX(node));
                        node.yCoordinatesProperty().setValue(springLayout.getY(node));
                    });


                    if (springLayout.done()) { drawFrame.cancel(); }

                    // TODO CREATE BREAK CONDITIONS

                }
            };
            */

            @Override
            protected String call() throws Exception {
                // Here is the main workload:

                boolean isConverged = false;

                while (!isConverged) {
                    Thread.sleep(50 );
                    springLayout.step();

                    graph.getVertices().forEach((node) -> {
                        node.xCoordinatesProperty().setValue(springLayout.getX(node));
                        node.yCoordinatesProperty().setValue(springLayout.getY(node));
                    });

                }

                return "FINISHED";
            }
        };

    }

    public void updateNode(MyVertex vertex) {
        springLayout.setLocation(vertex, vertex.getXCoordinates(), vertex.getYCoordinates());
    }

    public void monitoredStep() {

        int nodeCounter = 0;
        double sumX = 0;
        double sumY = 0;

        for (MyVertex v: graph.getVertices()){
            nodeCounter++;
            sumX += v.getXCoordinates();
            sumY += v.getYCoordinates();
        }

        springLayout.step();
    }






    /**
     * Creates a function that calculates an edge length based on the weight of the edge
     * FOr now, returns weight, in future this should be the correlation (bivariate dist.)
     * Gives preferred distance in integer values
     * @return edge length function used in the SpringLayout2
     */
    private Function<MyEdge, Integer> getMyEdgeLengthFunction() {

        Function<MyEdge, Integer> foo = new Function<MyEdge, Integer>() {
            @Override
            public Integer apply(MyEdge myEdge) {
                Double corr = myEdge.getCorrelation();

                Double weight = (min_length + ((max_length - min_length)/2)*(corr +1));

                //Double weight = (1 - Math.abs(corr)) * LENGTH_FACTOR;
                //Double weight = myEdge.getWeight();
                Integer intWeight = weight.intValue();
                return intWeight;
            }
        };
        return foo;
    }

}
