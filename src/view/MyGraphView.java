package view;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;

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
    JGraphSimpleLayout simpleLayout;

    public MyGraphView(MyGraph graph) {
        this.graph = graph;
        this.myVertexViewGroup = new Group();
        this.myEdgeViewGroup = new Group();

        simpleLayout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_RANDOM, MAX_X, MAX_Y);

        layoutVertices();
        drawEdges();
        drawNodes();
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
        JGraphFacade facade = new JGraphFacade(jGraph);
        simpleLayout.run(facade);

        // Lots of predefinded layout options are available

    }

    public Group getMyVertexViewGroup() {
        return myVertexViewGroup;
    }
}
