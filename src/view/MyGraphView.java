package view;


import com.google.common.base.Function;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import model.VertexSelectionModel;


import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by caspar on 19.06.17.
 */
public class MyGraphView extends Group {

    // TODO bind size to window size or something
    private int width = 1000;
    private int height =  800;

    private Group myVertexViewGroup;
    private Group myEdgeViewGroup;
    private MyGraph<MyVertex, MyEdge> graph;
    private SpringLayout2 springLayout;

    private VertexSelectionModel selectionModel;

    protected Function<MyEdge, Integer> myEdgeLengthFunction;


    public MyGraphView(MyGraph<MyVertex, MyEdge> graph) {
        this.graph = graph;
        this.myVertexViewGroup = new Group();
        this.myEdgeViewGroup = new Group();

        this.myEdgeLengthFunction = new Function<MyEdge, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable MyEdge myEdge) {
                Double weight = myEdge.getWeight();
                Integer intWeight = weight.intValue();
                return intWeight;
            }
        };

        this.springLayout = new SpringLayout2(graph, myEdgeLengthFunction);
        springLayout.setSize(new Dimension(width, height));


        drawNodes();
        drawEdges();
        startLayout();

        getChildren().add(myEdgeViewGroup);
        getChildren().add(myVertexViewGroup);

        // Add all Vertex to the selection Model and add Listener
        selectionModel = new VertexSelectionModel(graph.getVertices().toArray());
        addSelectionListener();

    }

    public void updateDimensions() {
        width = (int)getBoundsInParent().getWidth();
        height = (int)getBoundsInParent().getHeight();

    }


    public void drawEdges() {
        graph.getEdges().forEach((edge) -> {
            myEdgeViewGroup.getChildren().add(new MyEdgeView(edge));
        });
    }


    public void drawNodes() {
        graph.getVertices().forEach((node) -> {
            myVertexViewGroup.getChildren().add(new MyVertexView(node));
        });
    }

    public void startLayout() {
        updateDimensions();
        springLayout.initialize();
        graph.getVertices().forEach((node) -> {
            node.xCoordinatesProperty().setValue(springLayout.getX(node));
            node.yCoordinatesProperty().setValue(springLayout.getY(node));
        });
    }

    public void makeStep() {
        springLayout.step();
        graph.getVertices().forEach((node) -> {
            node.xCoordinatesProperty().setValue(springLayout.getX(node));
            node.yCoordinatesProperty().setValue(springLayout.getY(node));
        });

    }

    public void updateNodePosition(MyVertex vertex) {
        updateDimensions();
        springLayout.setLocation(vertex, vertex.getXCoordinates(), vertex.getYCoordinates());
    }

    public void addSelectionListener() {
        selectionModel.getSelectedItems().addListener((ListChangeListener) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Object o : c.getAddedSubList()) {
                        MyVertex vertex = (MyVertex) o;
                        vertex.isSelectedProperty().setValue(true);
                    }
                }
                if (c.wasRemoved()) {
                    for (Object o : c.getRemoved()) {
                        MyVertex vertex = (MyVertex) o;
                        vertex.isSelectedProperty().setValue(false);
                    }

                }
            }
        });
    }


    public Group getMyVertexViewGroup() {
        return myVertexViewGroup;
    }

    public MyGraph<MyVertex, MyEdge> getGraph() {
        return graph;
    }

    public VertexSelectionModel getSelectionModel() {
        return selectionModel;
    }
}
