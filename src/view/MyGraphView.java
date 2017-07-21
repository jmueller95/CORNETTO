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
    public SpringAnimationService animationService;
    private VertexSelectionModel selectionModel;


    protected Function<MyEdge, Integer> myEdgeLengthFunction;


    public MyGraphView(MyGraph<MyVertex, MyEdge> graph) {
        this.graph = graph;
        this.myVertexViewGroup = new Group();
        this.myEdgeViewGroup = new Group();
        this.animationService = new SpringAnimationService(graph, this);

        drawNodes();
        drawEdges();
        startLayout();

        getChildren().add(myEdgeViewGroup);
        getChildren().add(myVertexViewGroup);

        // Add all Vertex to the selection Model and add Listener
        selectionModel = new VertexSelectionModel(graph.getVertices().toArray());
        addSelectionListener();

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
        animationService.start();
    }

    public void updateNodePosition(MyVertex vertex) {
        animationService.updateNode(vertex);
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

    public void pauseAnimation(){
        animationService.cancel();
    }

    public void resumeAnimation(){
        animationService.resume();
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
