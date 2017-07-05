package view;


import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.scene.Group;


import java.awt.*;
import java.util.Iterator;

/**
 * Created by caspar on 19.06.17.
 */
public class MyGraphView extends Group {

    // TODO bind size to window size or something
    static final int MAX_X = 1000;
    static final int MAX_Y = 800;

    private Group myVertexViewGroup;
    private Group myEdgeViewGroup;
    private MyGraph<MyVertex, MyEdge> graph;
    //JGraphOrganicLayout organicLayout;
    //JGraphSpringLayout springLayout;
    //JGraphSimpleLayout simpleLayout;

    public MyGraphView(MyGraph<MyVertex, MyEdge> graph) {
        this.graph = graph;
        this.myVertexViewGroup = new Group();
        this.myEdgeViewGroup = new Group();

        //organicLayout = new JGraphOrganicLayout(new Rectangle(MAX_X, MAX_Y));
        //springLayout = new JGraphSpringLayout(1000);
        //simpleLayout = new JGraphSimpleLayout(2, 1000, 800);


        drawNodes();
        drawEdges();
        addPaneInteractivity();

        getChildren().add(myEdgeViewGroup);
        getChildren().add(myVertexViewGroup);
    }


    public void drawEdges() {
        graph.getEdges().forEach((edge) -> {
            myEdgeViewGroup.getChildren().add(new MyEdgeView((MyEdge) edge));
        });
    }


    public void drawNodes() {


    }

    public void addPaneInteractivity() {


    }

    public void addNodeInteractivity() {

    }



    public Group getMyVertexViewGroup() {
        return myVertexViewGroup;
    }
}
