package view;

import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;


/**
 * Created by caspar on 26.06.17.
 */
public class ViewPane extends StackPane {

    //constants
    private final static double ZOOM_FACTOR = 1.1;

    private double pressedX;
    private double pressedY;
    private double initTransX;
    private double initTransY;


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
        getChildren().addAll(bottomPane, topPane);

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
        selectRectangle = new Rectangle(0, 0, 0, 0);
        selectRectangle.setFill(Color.TRANSPARENT);
        selectRectangle.setStroke(Color.DARKSLATEGREY);
        selectRectangle.getStrokeDashArray().addAll(3.0, 7.0, 3.0, 7.0);
        topPane.getChildren().add(selectRectangle);
    }


    /**
     * Adds multiple mouse interactions to the graph view
     */
    private void addMouseInteractions() {
        // Register Location of Mouse button click for all Events
        setOnMousePressed((me) -> {

            pressedX = me.getSceneX();
            pressedY = me.getSceneY();

        });

        // Mouse clicked on elements of the VertexViewGroup:
        myGraphView.getMyVertexViewGroup().getChildren().forEach(o -> {

            MyVertexView vertexView = (MyVertexView) o;

            // Register translation of vertexVIew before starting Drag operation
            o.setOnMousePressed(me -> {
                initTransX = vertexView.translateXProperty().get();
                initTransY = vertexView.translateYProperty().get();
            });


            // Left Mouse Drag: Move Node
            o.setOnMouseDragged(me -> {

                setCursor(Cursor.MOVE);
                if (me.getButton() == MouseButton.PRIMARY) {

                    // Diagnostic output:
                    //System.out.print("MX: " + me.getSceneX() + " | " +  initTransX + " | " + viewTransformProperty.getValue().getTx() );
                    //System.out.println(" |||  MY: " + me.getSceneY() + " | " +  initTransY + " | " + viewTransformProperty.getValue().getTy() );

                    // Node translation takes into account: previous vertex translation (due to automatic layout), total pane Translation
                    double deltaX = (me.getSceneX() - 2 * viewTransformProperty.getValue().getTx());
                    double deltaY = (me.getSceneY() - 2 * viewTransformProperty.getValue().getTy());

                    // Translation value is divided by the initial scaling factor of the whole pane
                    vertexView.translateXProperty().set(((pressedX + deltaX) / viewTransformProperty.getValue().getMxx()) - initTransX);
                    vertexView.translateYProperty().set(((pressedY + deltaY) / viewTransformProperty.getValue().getMyy()) - initTransY);

                    me.consume();

                    // Update new Position in SpringLayout
                       myGraphView.updateNodePosition(vertexView.myVertex);
                }
            });

            // Select vertex on normal click without drag
            o.setOnMouseClicked(me -> {

                if (me.getButton() == MouseButton.PRIMARY && me.isStillSincePress()) {

                    System.out.println(me.isDragDetect());
                    myGraphView.getSelectionModel().toggleSelect(vertexView.getMyVertex());
                    me.consume();
                }
            });
        });


        bottomPane.setOnMouseDragged((me) -> {

            double deltaX = me.getSceneX() - pressedX;
            double deltaY = me.getSceneY() - pressedY;

            // Drag Graph with right Mouse Click
            if (me.getButton() == MouseButton.SECONDARY) {

                // Set "closed hand" cursor type for panning
                setCursor(Cursor.MOVE);

                Translate translate = new Translate(deltaX, deltaY);
                viewTransformProperty.setValue(translate.createConcatenation(viewTransformProperty.getValue()));
                pressedX = me.getSceneX();
                pressedY = me.getSceneY();
            }

            // Create selection rectangle with left MouseClick
            // Only create selection when mouse event target is NOT on a node
            if (me.getTarget().equals(bottomPane) && me.getButton() == MouseButton.PRIMARY) {

                //Set crosshair cursor
                setCursor(Cursor.CROSSHAIR);

                if (deltaX > 0) {
                    selectRectangle.setX(pressedX);
                    selectRectangle.setWidth(deltaX);
                } else {
                    selectRectangle.setX(pressedX + deltaX);
                    selectRectangle.setWidth(-deltaX);
                }

                if (deltaY > 0) {
                    selectRectangle.setY(pressedY);
                    selectRectangle.setHeight(deltaY);
                } else {
                    selectRectangle.setY(pressedY + deltaY);
                    selectRectangle.setHeight(-deltaY);
                }
            }
        });

        //Select Elements in Rectangle when Right Mouse is released
        setOnMouseReleased((me -> {

            myGraphView.getMyVertexViewGroup().getChildren().forEach(node -> {
                // Check if rectangle intersects node
                MyVertexView vertexView = (MyVertexView) node;
                // Cant get direct Scene bounds --> first get local bounds then apply scene transform
                Bounds nodeBounds = vertexView.getBoundsInLocal();
                Bounds nodeBoundsTransformed = vertexView.getLocalToSceneTransform().transform(nodeBounds);
                Bounds selectBounds = selectRectangle.getBoundsInParent();
                if (selectBounds.intersects(nodeBoundsTransformed)) {
                    System.out.println("Selection intersects node: " + vertexView);
                    // Add the nodes of all selected vertexViews to selection Model
                    myGraphView.getSelectionModel().select(vertexView.getMyVertex());
                }
                //System.out.println(nodeBounds + " | " +  nodeBoundsTransformed + " | " + selectBounds);
            });

            // Make rectangle invisible after selection end
            selectRectangle.setHeight(0);
            selectRectangle.setWidth(0);
            selectRectangle.setY(0);
            selectRectangle.setX(0);

            // Reset cursor type to normal
            setCursor(Cursor.DEFAULT);

        }));

        // Set on Mouse Wheel scroll
        setOnScroll((se -> {

            double scaleFactor = (se.getDeltaY() > 0) ? ZOOM_FACTOR : 1 / ZOOM_FACTOR;

            //double pivotX = (myGraphView.getBoundsInParent().getMaxX() - myGraphView.getBoundsInParent().getWidth()/2);
            //double pivotY = (myGraphView.getBoundsInParent().getMaxY() - myGraphView.getBoundsInParent().getHeight()/2);
            double pivotX = se.getSceneX();
            double pivotY = se.getSceneY();

            Scale scale = new Scale(scaleFactor, scaleFactor, pivotX, pivotY);
            viewTransformProperty.setValue(scale.createConcatenation(viewTransformProperty.getValue()));
        }));

        // TODO (?) Scroll with alt + / alt - or cmd + mouse?
        //if we have time! it's a very useful feature
    }
}
