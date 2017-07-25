package view;

import com.google.common.base.Function;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.lang.Thread;

import java.awt.*;

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
    private int frameRate = 50;

    private double maxLength = 800;
    private double minLength = 100;


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

        // Init settings
        springLayout.setStretch(0.9);
        springLayout.setRepulsionRange(80);
        springLayout.setForceMultiplier(0.8);

        springLayout.SpringVertexData()




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

                    long startTime = System.currentTimeMillis();
                    springLayout.step();
                    graph.getVertices().forEach((node) -> {
                        node.xCoordinatesProperty().setValue(springLayout.getX(node));
                        node.yCoordinatesProperty().setValue(springLayout.getY(node));
                    });
                    long deltaTime = System.currentTimeMillis()-startTime;

                    if (deltaTime < frameRate) {
                        Thread.sleep(frameRate - deltaTime);
                    }

//                    isConverged = springLayout.done();
//

                }
                this.done();
                return "FINISHED";

            }
        };

    }

    public void updateNode(MyVertex vertex) {
        springLayout.setLocation(vertex, vertex.getXCoordinates(), vertex.getYCoordinates());
    }

    public void setNodeRepulsion(int range) {

        springLayout.setRepulsionRange(range);
    }

    public void setStrechForce(double stretch) {
        springLayout.setStretch(stretch);
    }

    public void setForce(double force) {
        springLayout.setForceMultiplier(force);
    }

    public void setEdgeLengthLow(double val) {
        minLength = val;
    }

    public void setEdgeLengthHigh(double val){
        maxLength = val;
    }

    public void setFrameRate(int t) {
        frameRate = t;
    }


    /**
     * Creates a function that calculates an edge length based on the correlation value of the edge
     * Gives preferred distance in integer values
     * @return edge length function used in the SpringLayout2
     */
    private Function<MyEdge, Integer> getMyEdgeLengthFunction() {

        Function<MyEdge, Integer> foo = myEdge -> {
            Double corr = myEdge.getCorrelation();
            Double weight = (minLength + ((maxLength - minLength)/2)*(corr +1));
            return weight.intValue();
        };
        return foo;
    }

}
