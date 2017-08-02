package view;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.util.Pair;
import graph.MyEdge;
import graph.MyVertex;
import javafx.application.Platform;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;

/**
 * <h1>The class contains mostly of methods of the JUNG package - our own implementation of the spring layout</h1>
 * <p>
 * Merged SpringLayout and SpringLayout2 from the JUNG Package.
 * The following changes were done by us:
 * 1) Lowered sensitivity of done conditions
 * 2) Node positions are directly updated in the MyGraph class after each step
 * </p>
 *
 */

public class MySpringLayout<V, E> extends AbstractLayout<V,E> implements IterativeContext {

    private double stretch = 0.90;
    private Function<? super E, Integer> lengthFunction;
    private int repulsion_range_sq = 20 * 20;
    private double force_multiplier = 2.0;
    static final double INIT_FACTOR = 0.2;

    private LoadingCache<V, MySpringLayout.SpringVertexData> springVertexData =
            CacheBuilder.newBuilder().build(new CacheLoader<V, SpringVertexData>() {
                public SpringVertexData load(V vertex) {
                    return new MySpringLayout.SpringVertexData();
                }
            });

    private int currentIteration;
    private int averageCounter;
    private int loopCountMax = 4;
    private boolean done;

    private Point2D averageDelta = new Point2D.Double();

//    protected Map<V, SpringVertexData> springVertexData =
//    	new MapMaker().makeComputingMap(new Function<V,SpringVertexData>(){
//			public SpringVertexData apply(V arg0) {
//				return new SpringVertexData();
//			}});

    /**
     * Constructor for a SpringLayout for a raw graph with associated
     * dimension--the input knows how big the graph is. Defaults to the unit
     * length function.
     * @param g the graph on which the layout algorithm is to operate
     */
    @SuppressWarnings("unchecked")
    public MySpringLayout(Graph<V,E> g) {
        this(g, (Function<E,Integer>) Functions.<Integer>constant(30));
    }

    /**
     * Constructor for a SpringLayout for a raw graph with associated component.
     *
     * @param g the graph on which the layout algorithm is to operate
     * @param length_function provides a length for each edge
     */
    public MySpringLayout(Graph<V,E> g, Function<? super E, Integer> length_function)
    {
        super(g);
        this.lengthFunction = length_function;
    }

    /**
     * @return the current value for the stretch parameter
     */
    public double getStretch() {
        return stretch;
    }

    @Override
    public void setSize(Dimension size) {
        if(initialized == false)
             //setInitializer(new RandomLocationTransformer<V>(size));
             setInitializer((Function<V, Point2D>) mdsInitializer);
        super.setSize(size);
    }

    /**
     * <p>Sets the stretch parameter for this instance.  This value
     * specifies how much the degrees of an edge's incident vertices
     * should influence how easily the endpoints of that edge
     * can move (that is, that edge's tendency to change its length).
     *
     * <p>The default value is 0.70.  Positive values less than 1 cause
     * high-degree vertices to move less than low-degree vertices, and
     * values &gt; 1 cause high-degree vertices to move more than
     * low-degree vertices.  Negative values will have unpredictable
     * and inconsistent results.
     * @param stretch the stretch parameter
     */
    public void setStretch(double stretch) {
        this.stretch = stretch;
    }

    /**
     * Checks for Overall Nodemovement to determine if layout has converged
     * Greatly increased the threshhold for done from .001 to 1e-12
     */
    private void testAverageDeltas() {
        double dx = this.averageDelta.getX();
        double dy = this.averageDelta.getY();
        if(Math.abs(dx) < 1E-17 && Math.abs(dy) < 1E-17) {
            done = true;
            System.err.println("done, dx="+dx+", dy="+dy);
        }
        if(currentIteration > loopCountMax) {
            this.averageDelta.setLocation(0, 0);
            averageCounter = 0;
            currentIteration = 0;
        }
    }

    public int getRepulsionRange() {
        return (int)(Math.sqrt(repulsion_range_sq));
    }

    /**
     * Sets the node repulsion range (in drawing area units) for this instance.
     * Outside this range, nodes do not repel each other.  The default value
     * is 100.  Negative values are treated as their positive equivalents.
     * @param range the maximum repulsion range
     */
    public void setRepulsionRange(int range) {
        this.repulsion_range_sq = range * range;
    }

    public double getForceMultiplier() {
        return force_multiplier;
    }

    /**
     * Sets the force multiplier for this instance.  This value is used to
     * specify how strongly an edge "wants" to be its default length
     * (higher values indicate a greater attraction for the default length),
     * which affects how much its endpoints move at each timestep.
     * The default value is 1/3.  A value of 0 turns off any attempt by the
     * layout to cause edges to conform to the default length.  Negative
     * values cause long edges to get longer and short edges to get shorter; use
     * at your own risk.
     * @param force an energy field created by all living things that binds the galaxy together
     */
    public void setForceMultiplier(double force) {
        this.force_multiplier = force;
    }

    public void initialize() {
    }

    /**
     * Relaxation step. Moves all nodes a smidge.
     */
    public void step() {
        try {
            for(V v : getGraph().getVertices()) {
                if(!((MyVertex)v).isHidden()) {
                    MySpringLayout.SpringVertexData svd = springVertexData.getUnchecked(v);
                    if (svd == null) {
                        continue;
                    }
                    svd.dx /= 4;
                    svd.dy /= 4;
                    svd.edgedx = svd.edgedy = 0;
                    svd.repulsiondx = svd.repulsiondy = 0;
                }
            }
        } catch(ConcurrentModificationException cme) {
            step();
        }

        relaxEdges();
        calculateRepulsion();
        moveNodes();
        currentIteration++;
        //testAverageDeltas();
    }

    protected void relaxEdges() {
        try {
            for(E e : getGraph().getEdges()) {
                if (!((MyEdge)e).isHidden()){
                    Pair<V> endpoints = getGraph().getEndpoints(e);
                    V v1 = endpoints.getFirst();
                    V v2 = endpoints.getSecond();

                    Point2D p1 = apply(v1);
                    Point2D p2 = apply(v2);
                    if(p1 == null || p2 == null) continue;
                    double vx = p1.getX() - p2.getX();
                    double vy = p1.getY() - p2.getY();
                    double len = Math.sqrt(vx * vx + vy * vy);

                    double desiredLen = lengthFunction.apply(e);

                    // round from zero, if needed [zero would be Bad.].
                    len = (len == 0) ? .0001 : len;

                    double f = force_multiplier * (desiredLen - len) / len;

                    f = f * Math.pow(stretch, (getGraph().degree(v1) + getGraph().degree(v2) - 2));

                    // the actual movement distance 'dx' is the force multiplied by the
                    // distance to go.
                    double dx = f * vx;
                    double dy = f * vy;
                    MySpringLayout.SpringVertexData v1D, v2D;
                    v1D = springVertexData.getUnchecked(v1);
                    v2D = springVertexData.getUnchecked(v2);

                    v1D.edgedx += dx;
                    v1D.edgedy += dy;
                    v2D.edgedx += -dx;
                    v2D.edgedy += -dy;
                }
            }
        } catch(ConcurrentModificationException cme) {
            relaxEdges();
        }
    }

    protected void calculateRepulsion() {
        try {
            for (V v : getGraph().getVertices()) {
                if(!((MyVertex)v).isHidden()) {
                    if (isLocked(v)) continue;

                    MySpringLayout.SpringVertexData svd = springVertexData.getUnchecked(v);
                    if(svd == null) continue;
                    double dx = 0, dy = 0;

                    for (V v2 : getGraph().getVertices()) {
                        if (v == v2) continue;
                        Point2D p = apply(v);
                        Point2D p2 = apply(v2);
                        if(p == null || p2 == null) continue;
                        double vx = p.getX() - p2.getX();
                        double vy = p.getY() - p2.getY();
                        double distanceSq = p.distanceSq(p2);
                        if (distanceSq == 0) {
                            dx += Math.random();
                            dy += Math.random();
                        } else if (distanceSq < repulsion_range_sq) {
                            double factor = 1;
                            dx += factor * vx / distanceSq;
                            dy += factor * vy / distanceSq;
                        }
                    }
                    double dlen = dx * dx + dy * dy;
                    if (dlen > 0) {
                        dlen = Math.sqrt(dlen) / 2;
                        svd.repulsiondx += dx / dlen;
                        svd.repulsiondy += dy / dlen;
                    }
                }

            }
        } catch(ConcurrentModificationException cme) {
            calculateRepulsion();
        }
    }

    protected void moveNodes() {
        synchronized (getSize()) {
            try {
                for (V v : getGraph().getVertices()) {
                    if(!((MyVertex)v).isHidden()) {
                        if (isLocked(v)) continue;
                        SpringVertexData vd = springVertexData.getUnchecked(v);
                        if(vd == null) continue;
                        Point2D xyd = apply(v);

                        vd.dx += vd.repulsiondx + vd.edgedx;
                        vd.dy += vd.repulsiondy + vd.edgedy;

//                    int currentCount = currentIteration % this.loopCountMax;
//                    System.err.println(averageCounter+" --- vd.dx="+vd.dx+", vd.dy="+vd.dy);
//                    System.err.println("averageDelta was "+averageDelta);

                        averageDelta.setLocation(
                                ((averageDelta.getX() * averageCounter) + vd.dx) / (averageCounter+1),
                                ((averageDelta.getY() * averageCounter) + vd.dy) / (averageCounter+1)
                        );
//                    System.err.println("averageDelta now "+averageDelta);
//                    System.err.println();
                        averageCounter++;

                        // keeps nodes from moving any faster than 5 per time unit
                        xyd.setLocation(xyd.getX()+Math.max(-5, Math.min(5, vd.dx)),
                                xyd.getY()+Math.max(-5, Math.min(5, vd.dy)));

                        Dimension d = getSize();
                        int width = d.width;
                        int height = d.height;

                        if (xyd.getX() < 0) {
                            xyd.setLocation(0, xyd.getY());//                     setX(0);
                        } else if (xyd.getX() > width) {
                            xyd.setLocation(width, xyd.getY());             //setX(widthProperty);
                        }
                        if (xyd.getY() < 0) {
                            xyd.setLocation(xyd.getX(),0);//setY(0);
                        } else if (xyd.getY() > height) {
                            xyd.setLocation(xyd.getX(), height);      //setY(heightProperty);
                        }

                        // Updates the locations in MyVertex of the Graph
                        /** New runnable to make threadsafe updates to the main UI Thread **/
                        Runnable updateNodes = () -> {
                            ((MyVertex) v).xCoordinatesProperty().setValue(xyd.getX());
                            ((MyVertex) v).yCoordinatesProperty().setValue(xyd.getY());
                        };
                        Platform.runLater(updateNodes);
                    }
                }

            } catch(ConcurrentModificationException cme) {
                moveNodes();
            }
        }
    }


    protected static class SpringVertexData {
        protected double edgedx;
        protected double edgedy;
        protected double repulsiondx;
        protected double repulsiondy;

        /** movement speed, x */
        protected double dx;

        /** movement speed, y */
        protected double dy;
    }


    /**
     * Used for changing the size of the layout in response to a component's size.
     *  */
    public class SpringDimensionChecker extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            setSize(e.getComponent().getSize());
        }
    }


    /**
     * @return true
     */
    public boolean isIncremental() {
        return done;
    }

    /**
     * @return false
     */
    public boolean done() {
        return false;
    }

    /**
     * Reset done, Layout parameter continues running.
     * @param b
     */
    public void setDone(boolean b) {
        done = b;
    }

    /**
     * No effect.
     */
    public void reset() {
    }

    // TODO include function to resize MDS positions to the viewable are
    private Function<MyVertex, Point2D> mdsInitializer = (MyVertex myVertex) -> {

        double x = (myVertex.getXCoordinates() * size.getWidth())  * INIT_FACTOR + size.getWidth()/2;
        double y = myVertex.getYCoordinates() * size.getHeight() + INIT_FACTOR +  size.getHeight()/2;

        return new Point2D.Double(x, y);
    };


}
