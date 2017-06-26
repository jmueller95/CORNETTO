package graph;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.graph.JGraphSimpleLayout;
import javafx.scene.Group;
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
        simpleLayout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_RANDOM, MAX_X, MAX_Y);

        layoutVertices();
        drawEdges();
        drawNodes();
        addMouseControls();
    }

    public void layoutVertices() {
        // TODO Make something useful: for now random

    }

    public void drawEdges() {

        graph.edgeSet().forEach((edge) -> {
            getChildren().add(new MyEdgeView((MyEdge) edge));
        });
    }


    public void drawNodes() {

        graph.vertexSet().forEach((vertex) -> {
            getChildren().add(new MyVertexView((MyVertex) vertex));
        });
    }

    public void addMouseControls() {

        // TODO ScrollZoom, Pane Drag and Drop on Nodes and Graph
    }

    // Originally for Java Swing, maybe we can adapt this to work in FX?
    public void getPositionFromLayoutModel() {
        JGraphModelAdapter adapter = new JGraphModelAdapter(graph);
        JGraph jGraph = new JGraph(adapter);
        JGraphFacade facade = new JGraphFacade(jGraph);
        simpleLayout.run(facade);

        // Lots of predefinded layout options are available

    }



}
