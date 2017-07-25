package view;

import com.google.common.base.Function;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.renderers.CachingVertexRenderer;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.lang.Thread;



/**
 * Created by caspar on 16.07.17.
 */
public class SpringAnimationService extends Service {

    private MySpringLayout springLayout;
    private Function<MyEdge, Integer> myEdgeLengthFunction;
    private MyGraph<MyVertex, MyEdge> graph;

    private int width = 1000;
    private int height = 800;
    private int sleepTime = 50;

    private double maxLength = 500;
    private double minLength = 10;

    private boolean running;
    private boolean stop;
    private boolean manualSuspend;

    /* Used for synchronization. */
    private Object pauseObject = new String("PAUSE OBJECT");

    /**
     * Constructor for this class
     * @param graph used for Jung Layout
     * @param graphView providing mouse events to control the service status
     */
    public SpringAnimationService(MyGraph graph, MyGraphView graphView) {
        this.graph = graph;
        myEdgeLengthFunction = getMyEdgeLengthFunction();
        springLayout = new MySpringLayout(graph, myEdgeLengthFunction);
        springLayout.setSize(new Dimension(width, height));

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
            //Double weight = myEdge.getWeight();

            return weight.intValue();
        };
        return foo;
    }


    /**
     * Worker class for JavaFX concurrency
     * creates a task which includes all the main Stuff
     * @return empty
     */
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                /* Workload goes here */
                running = true;
                try {
                    while (!springLayout.done() && !stop) {
                        synchronized (pauseObject) {
                            while (manualSuspend && !stop) {
                                try {
                                    pauseObject.wait();
                                } catch (InterruptedException e) {
                                    // ignore
                                }
                            }
                        }
                        springLayout.step();

                        if (stop)
                            return null;

                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException ie) {
                            // ignore
                        }
                    }

                } finally {
                    manualSuspend = true;
                    springLayout.setDone(false);
                }
                return null;
            }

        };
    }


    /**
     * Updates location of given vertex in the layout.
     * @param vertex with custom position
     */
    public void updateNode(MyVertex vertex) {
        springLayout.setLocation(vertex, vertex.getXCoordinates(), vertex.getYCoordinates());
    }

    /**
     * Calculates layout steps until converged or for 500ms
     * Preconfigures the layout before starting the animation process.
     */
    public void prerelax() {
        manualSuspend = true;
        long timeNow = System.currentTimeMillis();
        while (System.currentTimeMillis() - timeNow < 500 && !springLayout.done()) {
            springLayout.step();
        }
        manualSuspend = false;
    }

    /**
     * Sets manualSuspend flag, pausing the Task
     */
    public void pause() {
        manualSuspend = true;
    }

    /**
     * Resume paused Task
     */
    public void resume() {
        manualSuspend = false;
        if(running == false) {
            //prerelax();
            relax();
        } else {
            synchronized(pauseObject) {
                pauseObject.notifyAll();
            }
        }
    }

    /**
     *
     */
    public void relax() {
        // in case its running
        stop();
        stop = false;
        createTask();
        start();
    }

    public synchronized void stop() {
        if(this.getState() != null) {
            manualSuspend = false;
            stop = true;
            running = false;
            // interrupt the relaxer, in case it is paused or sleeping
            // this should ensure that visRunnerIsRunning gets set to false
            try { this.cancel(); }
            catch(Exception ex) {
                // the applet security manager may have prevented this.
                // just sleep for a second to let the thread stop on its own
                try { Thread.sleep(1000); }
                catch(InterruptedException ie) {} // ignore
            }
            synchronized (pauseObject) {
                pauseObject.notifyAll();
            }
        }
    }

    /** This can be simplified later **/
    public void setNodeRepulsion(int range) {
        springLayout.setRepulsionRange(range);
    }

    public void setStretchForce(double stretch) {
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
        sleepTime = t;
    }

}