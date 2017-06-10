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
 * Created by julian on 15.05.17.
 */
public class TaxonId2CountCSVParser implements InputFile {
    private ArrayList<Sample> sampleList;
    private TaxonTree taxonTree;

    public TaxonId2CountCSVParser(TaxonTree taxonTree) {
        this.taxonTree = taxonTree;
        this.sampleList = new ArrayList<>();
    }

    //So far, every column is read and the corresponding number of sample objects is created.
    //TODO: In the final program, the user should be allowed to choose which columns are read
    //TODO: Add metadata!?
    @Override
    public ArrayList<Sample> parse(String filepath) throws IOException {
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

            //Add counts to datasets
            for (int i = 1; i <= numberOfDatasets; i++) {
                int currentSampleReadCount = (int) Double.parseDouble(lineSplit[i]);
                sampleList.get(i - 1).getTaxa2CountMap().put(currentTaxonNode, currentSampleReadCount);
            }
            line = reader.readLine();
        }


        return sampleList;
    }
}
