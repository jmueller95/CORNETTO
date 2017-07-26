package view;


import com.google.common.base.Function;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import model.VertexSelectionModel;


import javax.annotation.Nullable;
/**
 * Created by caspar on 19.06.17.
 */
public class MyGraphView extends Group {


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
        this.animationService = new SpringAnimationService(graph);
        drawNodes();
        drawEdges();

        getChildren().add(myEdgeViewGroup);
        getChildren().add(myVertexViewGroup);

        // Add all Vertex to the selection Model and add Listener
        selectionModel = new VertexSelectionModel(graph.getVertices().toArray());
        addSelectionListener();

        startLayout();

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


    public void startLayout() {
        animationService.start();
    }

    public void updateNodePosition(MyVertex vertex) {
        animationService.updateNode(vertex);
    }

    public void pauseAnimation(){
        animationService.cancel();
    }

    public void resumeAnimation(){
        animationService.restart();
    }

    public Group getMyVertexViewGroup() {
        return myVertexViewGroup;
    }

    public Group getMyEdgeViewGroup() {
        return myEdgeViewGroup;
    }

    public MyGraph<MyVertex, MyEdge> getGraph() {
        return graph;
    }

    public VertexSelectionModel getSelectionModel() {
        return selectionModel;
    }

}
