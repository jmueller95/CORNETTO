package UI;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import model.Sample;
import model.TaxonTree;
import sampleParser.BiomV1Parser;
import sampleParser.BiomV2Parser;
import sampleParser.ReadName2TaxIdCSVParser;
import sampleParser.TaxonId2CountCSVParser;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jm on 12.06.17.
 */
public class Presenter {
    private View view;
    private TaxonTree taxonTree;
    //TODO: Should this class actually have access to taxonTree or should this all be done somewhere else?

    public Presenter(View view, TaxonTree taxonTree) {
        this.view = view;
        this.taxonTree = taxonTree;
    }

    /**
     * Implements functionality for the menu items in the menu bar
     */
    public void setupMenuItems() {
        view.getImportBiomV1Item().setOnAction((e) -> {
            view.getFileChooser().setTitle("Import a BiomV1 file");
            view.getFileChooser().setSelectedExtensionFilter(new FileChooser.ExtensionFilter("BiomV1 file", ".biom"));
            String filepath = String.valueOf(view.getFileChooser().showOpenDialog(view.getScene().getWindow()));
            ArrayList<Sample> samples = new BiomV1Parser(taxonTree).parse(filepath);//TODO: Where should the samples be stored?
            view.getMessagesArea().appendText("\nSuccessfully imported BiomV1 file: " + filepath);
        });

        view.getImportBiomV2Item().setOnAction((e) -> {
            view.getFileChooser().setTitle("Import a BiomV2 file");
            view.getFileChooser().setSelectedExtensionFilter(new FileChooser.ExtensionFilter("BiomV2 file", ".biom"));
            String filepath = String.valueOf(view.getFileChooser().showOpenDialog(view.getScene().getWindow()));
            try {
                ArrayList<Sample> samples = new BiomV2Parser(taxonTree).parse(filepath);//TODO: Where should the samples be stored?
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            view.getMessagesArea().appendText("\nSuccessfully imported BiomV2 file: " + filepath);
        });

        view.getImportRead2IdItem().setOnAction((e) -> {
            view.getFileChooser().setTitle("Import a ReadName2TaxId CSV file");
            view.getFileChooser().setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV file", ".csv", ".txt"));
            String filepath = String.valueOf(view.getFileChooser().showOpenDialog(view.getScene().getWindow()));
            try {
                ArrayList<Sample> samples = new ReadName2TaxIdCSVParser(taxonTree).parse(filepath);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            view.getMessagesArea().appendText("\nSuccessfully imported ReadName2TaxId CSV file: " + filepath);
        });

        view.getImportId2CountItem().setOnAction((e) -> {

            view.getFileChooser().setTitle("Import a TaxonId2Count CSV file");
            view.getFileChooser().setSelectedExtensionFilter(new FileChooser.ExtensionFilter("CSV file", ".csv", ".txt"));
            String filepath = String.valueOf(view.getFileChooser().showOpenDialog(view.getScene().getWindow()));
            try {
                ArrayList<Sample> samples = new TaxonId2CountCSVParser(taxonTree).parse(filepath);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            view.getMessagesArea().appendText("\nSuccessfully imported TaxonId2Count CSV file: " + filepath);
        });

        view.getExitItem().setOnAction((e) -> {
            Platform.exit();
        });
    }


}
