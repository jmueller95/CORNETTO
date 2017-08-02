package sampleParser;

import model.Sample;
import model.TaxonNode;
import model.TaxonTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <h1>The class implements the parser for TaxonID2count files</h1>
 * <p>
 * The class is dependant on a TaxonTree to be available!
 * When done parsing the file a list of samples is returned.
 * </p>
 *
 * @see treeParser.TreeParser
 * @see Sample
 */
public class TaxonId2CountCSVParser implements InputFile {
    private ArrayList<Sample> sampleList;
    private TaxonTree taxonTree;

    public TaxonId2CountCSVParser(TaxonTree taxonTree) {
        this.taxonTree = taxonTree;
        this.sampleList = new ArrayList<>();
    }

    //So far, every column is read and the corresponding number of sample objects is created.
    @Override
    public ArrayList<Sample> parse(String filepath) throws IOException {
        sampleList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line = reader.readLine();
        String[] lineSplit = line.split(",");
        int numberOfDatasets = lineSplit.length - 1; //First column is taxId, rest are counts i.e. datasets
        for (int i = 0; i < numberOfDatasets; i++) {
            sampleList.add(new Sample());
        }

        //Get mapping of IDs to TaxonNodes from the TaxonTree
        HashMap<Integer, TaxonNode> treeStructure = taxonTree.getTreeStructure();

        while (line != null) {
            lineSplit = line.split(",");
            int currentTaxonId = Integer.parseInt(lineSplit[0]);

            TaxonNode currentTaxonNode = treeStructure.get(currentTaxonId);
            if (currentTaxonNode == null) {
                System.out.println("Couldn't find node:" + currentTaxonId);
            } else {
                //Add counts to datasets
                for (int i = 1; i <= numberOfDatasets; i++) {
                    int currentSampleReadCount = (int) Double.parseDouble(lineSplit[i]);
                    sampleList.get(i - 1).getTaxa2CountMap().put(currentTaxonNode, currentSampleReadCount);
                }
            }
            line = reader.readLine();
        }

        return sampleList;
    }
}
