package view;

import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
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
public class ViewPane extends AnchorPane {

    private final static double ZOOM_FACTOR = 1.1;

    private Rectangle selectRectangle;
    private Pane bottomPane;
    private Pane topPane;
    private MyGraphView myGraphView;
    private SubScene viewScene;
    private StackPane stackPane;

    private double pressedX;
    private double pressedY;
    private double initTransX;
    private double initTransY;

    private Property<Transform> viewTransformProperty;
    public StringProperty hoverInfo;

    public ViewPane(MyGraphView graphView) {

        // Setup Main Pane
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        this.setStyle("-fx-background-color : whitesmoke");

        // Create new scene with graphView
        this.myGraphView = graphView;
        bottomPane = new Pane();
        topPane = new Pane();
        topPane.setPickOnBounds(false);

        stackPane = new StackPane(bottomPane, myGraphView, topPane);
        viewScene = new SubScene(stackPane, this.getWidth(), this.getHeight());
        viewScene.widthProperty().bind(this.widthProperty());
        viewScene.heightProperty().bind(this.heightProperty());
        viewScene.setPickOnBounds(false);

        // Add StackPane in SubScene and add content.
        getChildren().addAll(viewScene);

        hoverInfo = new SimpleStringProperty("");

        // Setup interactions and view elements
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
        stackPane.getChildren().add(selectRectangle);
    }


    /**
     * Adds multiple mouse interactions to the graph view
     */
    private void addMouseInteractions() {
        // Register Location of Mouse button click for all Events
        stackPane.setOnMousePressed((me) -> {

            pressedX = me.getSceneX();
            pressedY = me.getSceneY();
            myGraphView.pauseAnimation();
            setCursor(Cursor.MOVE);
        });

        // Mouse clicked on elements of the VertexViewGroup:
        myGraphView.getMyVertexViewGroup().getChildren().forEach(o -> {

            MyVertexView vertexView = (MyVertexView) o;

            // OnMouseOver: Display Information in the Status Bar
            o.setOnMouseEntered(me -> {
                hoverInfo.setValue(vertexView.getMyVertex().getTaxonName());
            });

            o.setOnMouseExited(me -> {
                hoverInfo.setValue("");
            });

            // Register translation of vertexVIew before starting Drag operation
            o.setOnMousePressed(me -> {
                initTransX = vertexView.translateXProperty().get();
                initTransY = vertexView.translateYProperty().get();

            });


            // Left Mouse Drag: Move Node
            o.setOnMouseDragged(me -> {
                if (me.getButton() == MouseButton.PRIMARY) {

                    double deltaX = (pressedX - me.getSceneX());
                    double deltaY = (pressedY - me.getSceneY());

                    vertexView.translateXProperty().set(initTransX - (deltaX / viewTransformProperty.getValue().getMxx()));
                    vertexView.translateYProperty().set(initTransY - (deltaY/  viewTransformProperty.getValue().getMxx()));

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
        stackPane.setOnMouseReleased((me -> {

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
            myGraphView.resumeAnimation();
            setCursor(Cursor.DEFAULT);

        }));

        // Set on Mouse Wheel scroll
        stackPane.setOnScroll((se -> {

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
