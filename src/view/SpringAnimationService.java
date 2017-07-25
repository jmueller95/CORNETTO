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
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.lang.Thread;



/**
 * Created by caspar on 16.07.17.
 */
public class SpringAnimationService implements Runnable {

    private MySpringLayout springLayout;
    private Function<MyEdge, Integer> myEdgeLengthFunction;
    private MyGraph<MyVertex, MyEdge> graph;

    private VisRunner relaxer;

    private int width = 1000;
    private int height =  800;
    private int frameRate = 50;

    private double maxLength = 800;
    private double minLength = 100;

    protected boolean running;
    protected boolean stop;
    protected boolean manualSuspend;
    protected Thread thread;


    /**
     * Constructor for the AnimationService
     * @param graph GraphModel which is the basis of the Layout
     * @param graphView GraphView to get extent of the view.
     */
    public SpringAnimationService(MyGraph graph, MyGraphView graphView) {
        this.graph = graph;
        myEdgeLengthFunction = getMyEdgeLengthFunction();
        springLayout = new MySpringLayout(graph, myEdgeLengthFunction);
        springLayout.setSize(new Dimension(width, height));

    }



    public void updateNode(MyVertex vertex) {
        springLayout.setLocation(vertex, vertex.getXCoordinates(), vertex.getYCoordinates());
    }



    /**
     * how long the relaxer thread pauses between iteration loops.
     */
    protected long sleepTime = 100L;


    public void prerelax() {
        manualSuspend = true;
        long timeNow = System.currentTimeMillis();
        while (System.currentTimeMillis() - timeNow < 500 && !springLayout.done()) {
            springLayout.step();
        }
        manualSuspend = false;
    }

    public void pause() {
        manualSuspend = true;
    }

    public void relax() {
        // in case its running
        stop();
        stop = false;
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /**
     * Used for synchronization.
     */
    public Object pauseObject = new String("PAUSE OBJECT");

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

    public synchronized void stop() {
        if(thread != null) {
            manualSuspend = false;
            stop = true;
            // interrupt the relaxer, in case it is paused or sleeping
            // this should ensure that visRunnerIsRunning gets set to false
            try { thread.interrupt(); }
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

    @Override
    public void run() {
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
                    return;

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    // ignore
                }
            }

        } finally {
            running = false;
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
            //Double weight = myEdge.getWeight();

            return weight.intValue();
        };
        return foo;
    }

}
