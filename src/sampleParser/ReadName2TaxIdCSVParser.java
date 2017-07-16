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
public class ReadName2TaxIdCSVParser implements InputFile {
    private ArrayList<Sample> sampleList;
    private TaxonTree taxonTree;

    public ReadName2TaxIdCSVParser(TaxonTree taxonTree) {
        this.taxonTree = taxonTree;
        sampleList = new ArrayList<>();
    }

    /**
     * @param filepath
     * @return
     * @throws IOException
     */
    //TODO: Add metadata!?
    @Override
    public ArrayList<Sample> parse(String filepath) throws IOException {
        //Get Mapping of IDs to TaxonNodes from the TaxonTree
        HashMap<Integer, TaxonNode> treeStructure = taxonTree.getTreeStructure();
        //Initialize the object to be returned
        sampleList = new ArrayList<>();
        //This file format can only contain a single sample, so the list will contain only one element
        Sample sample = new Sample();
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line = reader.readLine();
        String[] lineSplit;
        int currentTaxonId;
        while (line != null) {
            lineSplit = line.split(",");
            currentTaxonId = Integer.parseInt(lineSplit[1]);
            //Get the TaxonNode for this sample
            TaxonNode currentTaxonNode = treeStructure.get(currentTaxonId);

            if (currentTaxonNode == null) {
                System.out.println("Couldn't find node:" + currentTaxonId);
            } else {
                //Check if this taxon is already in the sample
                HashMap<TaxonNode, Integer> taxa2CountMap = sample.getTaxa2CountMap();
                if (taxa2CountMap.containsKey(currentTaxonNode)) {
                    //Increment the count of this taxon in the sample by one
                    taxa2CountMap.put(currentTaxonNode, taxa2CountMap.get(currentTaxonNode) + 1);
                } else {
                    //Else add the taxon to the sample and set its count to one
                    taxa2CountMap.put(currentTaxonNode, 1);
                }
            }
            line = reader.readLine();
        }

        //Only add sample if it's not empty
        if (sample.getTaxa2CountMap().size() > 0) {
            sampleList.add(sample);
        }
        return sampleList;
    }
}
