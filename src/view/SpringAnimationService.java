package view;

import com.google.common.base.Function;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.awt.Dimension;
import java.lang.Thread;



/**
 * Created by caspar on 16.07.17.
 * Creates a javaFX Service for Layout calculations in a separate thread.
 */
public class SpringAnimationService extends Service {

    private MySpringLayout springLayout;
    private Function<MyEdge, Integer> myEdgeLengthFunction;
    private MyGraph graph;

    private int width = 1000;
    private int height = 800;
    private int sleepTime = 50;

    private double maxLength = 500;
    private double minLength = 10;

    private boolean running;
    private boolean stop;
    private boolean manualSuspend;
    private boolean prerelaxDone = false;

    /* Used for synchronization. */
    private Object pauseObject = new String("PAUSE OBJECT");

    /**
     * Constructor for this class
     * @param graph used for Jung Layout
     */
    public SpringAnimationService(MyGraph graph) {
        this.graph = graph;
        myEdgeLengthFunction = getMyEdgeLengthFunction();
        springLayout = new MySpringLayout(graph, myEdgeLengthFunction);
        springLayout.setSize(new Dimension(width, height));
    }

    /**
     * Creates a function that calculates an edge length based on the correlation value of the edge
     * Correlation of -1 results in minmal length, Corr = +1 is maximal length
     * Gives preferred distance in integer values
     * @return edge length function used in the SpringLayout2
     */
    private Function<MyEdge, Integer> getMyEdgeLengthFunction() {

        Function<MyEdge, Integer> foo = myEdge -> {
            Double corr = myEdge.getCorrelation();

            // Corr = -1 --> Edgelength = minLength
            // Corr =  1 --> Edgelength = maxLength
            Double weight = (minLength + ((maxLength - minLength)/2)*(1 - corr));
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

                // Check for prerelax
                if (!prerelaxDone) {
                    long timeNow = System.currentTimeMillis();
                    while (System.currentTimeMillis() - timeNow < 500 && !springLayout.done()) {
                        springLayout.step();
                    }
                    springLayout.setDone(false);
                    prerelaxDone = true;
                }

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


    @Override
    public void start() {
        super.start();
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    public void restart() {
        super.cancel();
        super.reset();

        springLayout = new MySpringLayout(graph, myEdgeLengthFunction);
        springLayout.setSize(new Dimension(width, height));
        setAllLocations();

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

    public void relax() {
        // in case its running
        stop();
        stop = false;
        createTask();
        start();
    }

    public void pause() {
        manualSuspend = true;
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

    private void setAllLocations() {
        for (Object v: graph.getVertices()) {
            MyVertex mV = (MyVertex)v;
            springLayout.setLocation(mV, mV.getXCoordinates(), mV.getYCoordinates());
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
        restart();
    }

    public void setEdgeLengthHigh(double val){
        maxLength = val;
        restart();
    }

    public void setFrameRate(int t) {
        sleepTime = t;
    }

}