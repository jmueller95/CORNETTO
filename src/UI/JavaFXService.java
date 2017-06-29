package UI;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by Zeth on 26.06.2017.
 *
 * LIKELY DEPRECATED
 */
public class JavaFXService extends Application {
    TreePreloadService myService;

    @Override
    public void start(Stage primaryStage) {

        final ProgressBar progressBar = new ProgressBar();
        final Label labelCount = new Label();
        final Label labelState = new Label();
        final Label labelSucceeded = new Label();

        myService = new TreePreloadService();

        myService.setOnSucceeded(t -> labelSucceeded.setText("OnSucceeded"));

        myService.setOnRunning(t -> labelSucceeded.setText("OnRunning"));

        myService.setOnFailed(t -> labelSucceeded.setText("OnFailed"));

        progressBar.progressProperty().bind(myService.progressProperty());
        labelCount.textProperty().bind(myService.messageProperty());

        Button btnStart = new Button("Start parsing the tree");
        btnStart.setOnAction(t -> myService.start());

        Button btnReadTaskState = new Button("Read Service State");
        btnReadTaskState.setOnAction(t -> labelState.setText(myService.getState().toString()));

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(5, 5, 5, 5));
        vBox.setSpacing(5);
        vBox.getChildren().addAll(
                progressBar,
                labelCount,
                btnStart,
                btnReadTaskState,
                labelState,
                labelSucceeded);

        StackPane root = new StackPane();
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Parse the tree");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
