package UI;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;

/**
 * Created by jm on 12.06.17.
 */
public class View extends GridPane {
    private MenuBar menuBar;
    private Menu fileMenu;
    private Menu importFileMenu;
    private MenuItem importBiomV1Item, importBiomV2Item, importRead2IdItem, importId2CountItem, exitItem;
    private FileChooser fileChooser;
    private Pane graphDisplay;
    private TextArea messagesArea;

    /**
     *creates the required fields for the view
     */
    public View() {
        menuBar = new MenuBar();
        fileMenu = new Menu("File");
        importFileMenu = new Menu("Import");
        importBiomV1Item = new MenuItem("Import BIOMV1 file");
        importBiomV2Item = new MenuItem("Import BIOMV2 file");
        importRead2IdItem = new MenuItem("Import ReadName2TaxonId CSV file");
        importId2CountItem = new MenuItem("Import TaxonId2Count CSV file");
        exitItem = new MenuItem("Exit");
        importFileMenu.getItems().addAll(importBiomV1Item, importBiomV2Item, importRead2IdItem, importId2CountItem, exitItem);
        fileMenu.getItems().add(importFileMenu);
        menuBar.getMenus().addAll(fileMenu);
        fileChooser = new FileChooser();
        graphDisplay = new Pane();
        messagesArea = new TextArea();

        initFields();
        createLayout();
    }

    /**
     * initializes all fields to their default values
     */
    private void initFields() {
        messagesArea.setEditable(false);
        messagesArea.setText("Welcome to the Correlation Network Analysis Tool! Please import your samples to begin!");

    }

    /**
     * creates the layout of the GUI
     * sets the screen size of the GUI
     */
    private void createLayout() {
        //We want the app to take all the height and three quarters of the width of the screen
        double width = Screen.getPrimary().getVisualBounds().getWidth() * 3 / 4;
        double height = Screen.getPrimary().getVisualBounds().getHeight();
        setPrefSize(width, height);
        setHgap(10);
        setVgap(10);
        add(menuBar, 0, 0);
        graphDisplay.setPrefSize(width, height * 5 / 8);
        add(graphDisplay, 0, 1);
        messagesArea.setPrefSize(width, height * 1 / 4);
        add(messagesArea, 0, 2);
    }

    public MenuItem getImportBiomV1Item() {
        return importBiomV1Item;
    }

    public MenuItem getImportBiomV2Item() {
        return importBiomV2Item;
    }

    public MenuItem getImportRead2IdItem() {
        return importRead2IdItem;
    }

    public MenuItem getImportId2CountItem() {
        return importId2CountItem;
    }

    public MenuItem getExitItem() {
        return exitItem;
    }

    public TextArea getMessagesArea() {
        return messagesArea;
    }

    public Pane getGraphDisplay() {
        return graphDisplay;
    }

    public FileChooser getFileChooser() {

        return fileChooser;
    }
}
