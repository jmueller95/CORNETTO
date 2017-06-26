package view;

import graph.MyEdge;
import graph.MyGraph;
import graph.MyGraphView;
import graph.MyVertex;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.TaxonNode;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by caspar on 25.06.17.
 */
public class ViewTest extends Application{

    private MyGraph graph = new MyGraph();


    @Before
    public void setUp() throws Exception {
        //Create new sample Graph
        MyVertex v1 = new MyVertex("Content"); //Has string as content
        MyVertex v2 = new MyVertex(42.0); //Has double as content
        MyVertex v3 = new MyVertex(new TaxonNode(1234, "rank", 1)); //Has TaxonNode as content
        MyEdge edge12 = new MyEdge(v1, v2);
        MyEdge edge23 = new MyEdge(v2, v3);
        this.graph.addVertex(v1);
        this.graph.addVertex(v2);
        this.graph.addVertex(v3);
        this.graph.addEdge(edge12);
        this.graph.addEdge(edge23);

    }

    @Test
    public void testView () throws InterruptedException{
        launch();

    }

    // This comes from overriding the standard JavaFX Start Method
    @Override
    public void start(Stage primaryStage) throws Exception {
        setUp();
        primaryStage.setTitle("Graph View Test");

        Pane root = new Pane();
        MyGraphView myGraphView = new MyGraphView(graph);

        root.getChildren().add(myGraphView);
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }
}

