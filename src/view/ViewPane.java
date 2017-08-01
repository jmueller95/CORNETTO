package view;

import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
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

import java.util.HashMap;


/**
 * <h1>Class which contains the View Pane for the graph</h1>
 * <p>
 * The class contains the method for the selection and transformation of
 * selected items such as nodes. The class contains the methods for the mouse interactions.
 * </p>
 *
 * @see MySpringLayout
 * @see SpringAnimationService
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
    private double mousePressedX = 0;
    private double mousePressedY = 0;
    private double initTransX;
    private double initTransY;
    private HashMap<MyVertexView, Double[]> groupNodeInitLocation;

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

        // Bind LayoutWidth to this here
        myGraphView.animationService.widthProperty.bind(this.widthProperty());
        myGraphView.animationService.heightProperty.bind(this.heightProperty());

        hoverInfo = new SimpleStringProperty("");
        this.groupNodeInitLocation = new HashMap<>();

        // Setup interactions and view elements
        setupTransforms();
        initRectangle();
        addMouseInteractions();
    }

    /**
     * sets up transforming of selections
     */
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
        stackPane.setOnMousePressed((me) -> {

            pressedX = me.getX();
            pressedY = me.getY();
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

            // Register translation of vertexView before starting Drag operation
            o.setOnMousePressed(me -> {
                initTransX = vertexView.translateXProperty().get();
                initTransY = vertexView.translateYProperty().get();

                mousePressedX = me.getSceneX();
                mousePressedY = me.getSceneY();

            });

            // Left Mouse Drag: Move Node
            o.setOnMouseDragged(me -> {
                if (me.getButton() == MouseButton.PRIMARY) {

                    double deltaX = (mousePressedX - me.getSceneX());
                    double deltaY = (mousePressedY - me.getSceneY());

                    vertexView.translateXProperty().set(initTransX - deltaX);
                    vertexView.translateYProperty().set(initTransY - deltaY);

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

        // Stores initial location of nodes in hashmap for group translate
        bottomPane.setOnMousePressed((me) -> {
            mousePressedX = me.getSceneX();
            mousePressedY = me.getSceneY();

            if (me.getTarget().equals(bottomPane) && me.getButton() == MouseButton.PRIMARY && me.isShiftDown()) {

                for (Node node : myGraphView.getMyVertexViewGroup().getChildren()) {

                    MyVertexView vW = (MyVertexView) node;
                    // Check if selected:
                    groupNodeInitLocation.put(vW, new Double[] {vW.translateXProperty().get(),
                            vW.translateYProperty().get()});
                }
            }
        });

        bottomPane.setOnMouseDragged((me) -> {

            double deltaX = me.getSceneX() - mousePressedX;
            double deltaY = me.getSceneY() - mousePressedY;

            // Drag Graph with right Mouse Click
            if (me.getButton() == MouseButton.SECONDARY) {

                // Set "closed hand" cursor type for panning
                setCursor(Cursor.MOVE);

                Translate translate = new Translate(deltaX, deltaY);
                viewTransformProperty.setValue(translate.createConcatenation(viewTransformProperty.getValue()));

                mousePressedX = me.getSceneX();
                mousePressedY = me.getSceneY();
            }


            // Create selection rectangle with left MouseClick
            // Only create selection when mouse event target is NOT on a node
            if (me.getTarget().equals(bottomPane) && me.getButton() == MouseButton.PRIMARY && !me.isShiftDown()) {

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

            // Drag selected Nodes with Left MB + Shift
            if (me.getTarget().equals(bottomPane) && me.getButton() == MouseButton.PRIMARY && me.isShiftDown()) {

                for (Node node : myGraphView.getMyVertexViewGroup().getChildren()) {

                    MyVertexView vW = (MyVertexView) node;
                    // Check if selected:
                    if (vW.myVertex.isSelectedProperty().get()) {

                            double x = groupNodeInitLocation.get(vW)[0];
                            double y = groupNodeInitLocation.get(vW)[1];
                            vW.translateXProperty().set(x + deltaX);
                            vW.translateYProperty().set(y + deltaY);
                        }
                }
                //me.consume();
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

            // Pivot Point is under the Mouse button
            double pivotX = se.getX();
            double pivotY = se.getY();

            // Set minimal zoom:

            Scale scale = new Scale(scaleFactor, scaleFactor, pivotX, pivotY);
            viewTransformProperty.setValue(scale.createConcatenation(viewTransformProperty.getValue()));
        }));

        // Deselect all nodes when clicked on Background without dragging
        stackPane.setOnMouseClicked((me -> {
            if (me.getButton() == MouseButton.PRIMARY && me.isStillSincePress()) {
                myGraphView.getSelectionModel().clearSelection();
            }
        }));
    }
}
