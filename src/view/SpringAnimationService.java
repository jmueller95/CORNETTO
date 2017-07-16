package view;

import com.google.common.base.Function;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by caspar on 16.07.17.
 */
public class SpringAnimationService extends Service<String> {

    private SpringLayout2 springLayout2;
    private Function<MyEdge, Integer> myEdgeLengthFunction;
    private MyGraph<MyVertex, MyEdge> graph;
    private MyGraphView graphView;
    private int width = 1000;
    private int height =  800;

    /**
     * Constructor for the AnimationService
     * @param graph GraphModel which is the basis of the Layout
     * @param graphView GraphView to get extent of the view.
     */
    public SpringAnimationService(MyGraph graph, MyGraphView graphView) {
        this.graph = graph;
        this.graphView = graphView;
    }

    /**
     * Initializes Service before use. Needs graph as argument.
     * @return
     */
    @Override
    protected Task<String> createTask() {

        myEdgeLengthFunction = getMyEdgeLengthFunction();
        springLayout2 = new SpringLayout2(graph, myEdgeLengthFunction);
        springLayout2.setSize(new Dimension(width, height));

        return new Task<String>() {

            Timer timer = new Timer();

            TimerTask drawFrame = new TimerTask() {
                @Override
                public void run() {

                    width = (int)graphView.getBoundsInParent().getWidth();
                    height = (int)graphView.getBoundsInParent().getHeight();

                    // Calculate next iteration and move nodes
                    springLayout2.step();
                    System.out.println("STEP DONE");
                    graph.getVertices().forEach((node) -> {
                        node.xCoordinatesProperty().setValue(springLayout2.getX(node));
                        node.yCoordinatesProperty().setValue(springLayout2.getY(node));
                    });

                    // TODO CREATE BREAK CONDITIONS

                }
            };

            @Override
            protected String call() throws Exception {
                // Here is the main workload:

                timer.scheduleAtFixedRate(drawFrame, 0l, 200l);
                return "FINISHED";
            }


        };
    }

    public void updateNode(MyVertex vertex) {
        springLayout2.setLocation(vertex, vertex.getXCoordinates(), vertex.getYCoordinates());
    }


    /**
     * Creates a function that calculates an edge length based on the weight of the edge
     * FOr now, returns weight, in future this should be the correlation (bivariate dist.)
     * Gives preferred distance in integer values
     * @return edge length function used in the SpringLayout2
     */
    private Function<MyEdge, Integer> getMyEdgeLengthFunction() {

        Function<MyEdge, Integer> foo = new Function<MyEdge, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable MyEdge myEdge) {
                Double weight = myEdge.getWeight();
                Integer intWeight = weight.intValue();
                return intWeight;
            }
        };
        return foo;
    }

}
