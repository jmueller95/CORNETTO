package view;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import com.jgraph.layout.organic.JGraphOrganicLayout;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyGraphFacade;
import graph.MyVertex;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;

import java.awt.*;
import java.util.Map;

/**
 * Created by caspar on 19.06.17.
 */
public class MyGraphView extends Group {

    // TODO bind size to window size or sonething
    static final int MAX_X = 1000;
    static final int MAX_Y = 800;

    private Group myVertexViewGroup;
    private Group myEdgeViewGroup;
    private MyGraph graph;
    JGraphOrganicLayout organicLayout;

    public MyGraphView(MyGraph graph) {
        this.graph = graph;
        this.myVertexViewGroup = new Group();
        this.myEdgeViewGroup = new Group();

        organicLayout = new JGraphOrganicLayout(new Rectangle(MAX_X, MAX_Y));

        layoutVertices();
        drawEdges();
        drawNodes();
        getPositionFromLayoutModel();
        addPaneInteractivity();

        getChildren().add(myEdgeViewGroup);
        getChildren().add(myVertexViewGroup);
    }

    public void layoutVertices() {
        // TODO Make something useful: for now random

    }

    public void drawEdges() {
        graph.edgeSet().forEach((edge) -> {
            myEdgeViewGroup.getChildren().add(new MyEdgeView((MyEdge) edge));
        });
    }


    public void drawNodes() {
        graph.vertexSet().forEach((vertex) -> {
            myVertexViewGroup.getChildren().add(new MyVertexView((MyVertex) vertex));
        });
    }

    public void addPaneInteractivity() {


    }

    public void addNodeInteractivity() {

    }

    // Originally for Java Swing, maybe we can adapt this to work in FX?
    public void getPositionFromLayoutModel() {
        JGraphModelAdapter adapter = new JGraphModelAdapter(graph);
        JGraph jGraph = new JGraph(adapter);
        MyGraphFacade myGraphFacade = new MyGraphFacade(jGraph);
        organicLayout.run(myGraphFacade);
        double[][] locations =  myGraphFacade.getAllLocations();
        System.out.println(locations.toString());
    }

    public Group getMyVertexViewGroup() {
        return myVertexViewGroup;
    }
}
