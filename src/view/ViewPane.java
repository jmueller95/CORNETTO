package view;

import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import graph.MyGraph;
import graph.MyVertex;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventDispatchChain;
import javafx.scene.Group;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import model.VertexSelectionModel;


/**
 * Created by caspar on 26.06.17.
 */
public class ViewPane extends StackPane{

    private final static double ZOOM_FACTOR = 1.1;

    private double downX;
    private double downY;
    private Rectangle selectRectangle;
    private Pane topPane;
    private Pane bottomPane;
    private MyGraphView myGraphView;

    private Property<Transform> viewTransformProperty;



    ViewPane(MyGraphView graphView) {

        topPane = new Pane();
        topPane.setPickOnBounds(false);

        bottomPane = new Pane();
        this.myGraphView = graphView;
        bottomPane.getChildren().add(myGraphView);

        //myGraphView.getTransforms().addAll(translate, scale);
        getChildren().addAll(topPane, bottomPane);

        setupTransforms();
        initRectangle();
        addMouseInteractions();


    }



    private void setupTransforms() {
        viewTransformProperty = new SimpleObjectProperty<>(new Transform() {
            @Override
            public void impl_apply(Affine3D t) {

            }

            @Override
            public BaseTransform impl_derive(BaseTransform t) {
                return null;
            }
        });
        viewTransformProperty.addListener((observable, oldValue, newValue) -> {
            myGraphView.getTransforms().setAll(newValue);
        });
    }

    private void initRectangle() {
        selectRectangle = new Rectangle(0,0, 0, 0);
        selectRectangle.setFill(Color.TRANSPARENT);
        selectRectangle.setStroke(Color.DARKSLATEGREY);
        selectRectangle.getStrokeDashArray().addAll(3.0, 7.0, 3.0, 7.0);
        topPane.getChildren().add(selectRectangle);
    }


    private void addMouseInteractions() {
        // Register Location of Mouse button click
        setOnMousePressed((me) -> {

            downX = me.getSceneX();
            downY = me.getSceneY();

        });

        // Mouse clicked on elements of the VertexViewGroup:
        myGraphView.setOnMouseClicked((me) ->{

            // Target is node? --> Toggle select
            System.out.println(me.getPickResult());

            // Target is edge? --> Toggle select both ends
        });

        bottomPane.setOnMouseDragged((me) -> {

            double deltaX = me.getSceneX() - downX;
            double deltaY = me.getSceneY() - downY;

            // Drag Graph with right Mouse Click
            if (me.getButton() == MouseButton.SECONDARY) {

                Translate translate = new Translate(deltaX, deltaY);
                viewTransformProperty.setValue(translate.createConcatenation(viewTransformProperty.getValue()));
                downX = me.getSceneX();
                downY = me.getSceneY();

            }


            // Create selection rectangle with left MouseClick
            // Only create selection when mouse event target is NOT on a node
            if (me.getTarget().equals(bottomPane) && me.getButton() == MouseButton.PRIMARY) {
                if (deltaX > 0) {
                    selectRectangle.setX(downX);
                    selectRectangle.setWidth(deltaX);
                } else {
                    selectRectangle.setX(downX + deltaX);
                    selectRectangle.setWidth(-deltaX);
                }

                if (deltaY > 0) {
                    selectRectangle.setY(downY);
                    selectRectangle.setHeight(deltaY);
                } else {
                    selectRectangle.setY(downY + deltaY);
                    selectRectangle.setHeight(-deltaY);
                }
            }
        });

        //Select Elements in Rectangle when Right Mouse is released
        setOnMouseReleased((me -> {

            // TODO selection only working when node has not been moved before
            myGraphView.getMyVertexViewGroup().getChildren().forEach(node -> {
                // Check if rectangle intersects node
                MyVertexView vertexView = (MyVertexView) node;
                if (vertexView.getBoundsInParent().intersects(selectRectangle.getBoundsInParent())) {
                    System.out.println("Selection intersects node: " + vertexView);
                    // Add the nodes of all selected vertexViews to selection Model
                    myGraphView.getSelectionModel().select(vertexView.getMyVertex());
                }
            });

            // Make rectangle invisible after selection end
            selectRectangle.setHeight(0);
            selectRectangle.setWidth(0);
            selectRectangle.setY(0);
            selectRectangle.setX(0);

        }));

        // Set on Mouse Wheel scroll
        setOnScroll((se -> {

            double scaleFactor = (se.getDeltaY() > 0) ? ZOOM_FACTOR : 1/ ZOOM_FACTOR;

            //double pivotX = (myGraphView.getBoundsInParent().getMaxX() - myGraphView.getBoundsInParent().getWidth()/2);
            //double pivotY = (myGraphView.getBoundsInParent().getMaxY() - myGraphView.getBoundsInParent().getHeight()/2);
            double pivotX = se.getSceneX();
            double pivotY = se.getSceneY();

            Scale scale = new Scale(scaleFactor, scaleFactor, pivotX, pivotY);
            viewTransformProperty.setValue(scale.createConcatenation(viewTransformProperty.getValue()));
        }));

        // TODO (?) Scroll with alt + / alt - or cmd + mouse?
    }

}
