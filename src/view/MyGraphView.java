package view;


import com.google.common.base.Function;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import model.VertexSelectionModel;


import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Created by caspar on 19.06.17.
 */
public class MyGraphView extends Group {

    private Group myVertexViewGroup;
    private Group myEdgeViewGroup;
    private MyGraph<MyVertex, MyEdge> graph;
    public SpringAnimationService animationService;
    private VertexSelectionModel selectionModel;

    // Properties changed by GUI, influencing the graph Display
    public BooleanProperty pausedProperty;

    protected Function<MyEdge, Integer> myEdgeLengthFunction;



    public MyGraphView(MyGraph<MyVertex, MyEdge> graph) {
        this.graph = graph;
        this.myVertexViewGroup = new Group();
        this.myEdgeViewGroup = new Group();
        this.animationService = new SpringAnimationService(graph);

        this.pausedProperty = new SimpleBooleanProperty(false);

        drawNodes();
        drawEdges();

        getChildren().add(myEdgeViewGroup);
        getChildren().add(myVertexViewGroup);

        // Add all Vertex to the selection Model and add Listener
        selectionModel = new VertexSelectionModel(graph.getVertices().toArray());
        addSelectionListener();
        addPausedListener();

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

    public void addLayoutDimensionBinding() {


    }

    /**
     * Sets Colour Attribute and Palette in the MyVertexView classes when called on User Input
     * @param attribute in String Format, should be in AttributeMap of the MyVertex class
     * @param palette Pakette item used for the colouring
     */
    public void setNodeColourAttributes(String attribute, Palette palette) {
        for (Node n : myVertexViewGroup.getChildren()) {

            MyVertexView vW = (MyVertexView) n;
            vW.colourAttribute.setValue(attribute);
            vW.colourProperty.setValue(palette);

        }

    }


    public void addPausedListener() {
        pausedProperty.addListener(e -> {
            if (pausedProperty.get()) {
                pauseAnimation();
            } else resumeAnimation();
        });
    }


    public void startLayout() {
        animationService.start();
        // If paused: cancel directly afterwards
        if (pausedProperty.get()) pauseAnimation();
    }

    public void updateNodePosition(MyVertex vertex) {
        animationService.updateNode(vertex);
    }

    public void pauseAnimation(){
        animationService.cancel();
    }

    public void resumeAnimation(){
        if (!pausedProperty.get()) animationService.restart();

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
