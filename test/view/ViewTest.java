package view;

import graph.MyEdge;
import graph.MyGraph;
import graph.MyVertex;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.TaxonNode;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by caspar on 25.06.17.
 */
public class ViewTest extends Application {

    private MyGraph<MyVertex, MyEdge> graph = new MyGraph<MyVertex, MyEdge>();

    @Before
    public void setUp() throws Exception {

        //Create new sample Graph
        MyVertex v1 = new MyVertex(new TaxonNode(1, "rank", 1),3);
        MyVertex v2 = new MyVertex(new TaxonNode(2, "rank", 1),3);
        MyVertex v3 = new MyVertex(new TaxonNode(3, "rank", 1),3);
        MyVertex v4 = new MyVertex(new TaxonNode(4, "rank", 1),3);
        MyEdge edge12 = new MyEdge(v1, v2, 100);
        MyEdge edge23 = new MyEdge(v2, v3, 200);
        MyEdge edge24 = new MyEdge(v4, v2, 303);
        this.graph.addVertex(v1);
        this.graph.addVertex(v2);
        this.graph.addVertex(v3);
        this.graph.addVertex(v4);
        this.graph.addEdge(edge12, v1, v2);
        this.graph.addEdge(edge23, v2, v3);
        this.graph.addEdge(edge24, v2, v4);

    }

    @Test
    public void testView() throws InterruptedException {
        launch();

    }

    // This comes from overriding the standard JavaFX Start Method
    @Override
    public void start(Stage primaryStage) throws Exception {
        setUp();
        primaryStage.setTitle("Graph View Test");

        MyGraphView myGraphView = new MyGraphView(graph);
        ViewPane root = new ViewPane(myGraphView);

        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();

        myGraphView.startLayout();

        // Open Scenic VIew Diagnostics Tool
        //ScenicView.show(root);
    }
}

