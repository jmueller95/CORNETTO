package sampleParser;

import model.Sample;
import model.TaxonNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by julian on 15.05.17.
 */
public class ReadName2TaxIdCSVParser implements InputFile {
    /**
     * @param filepath
     * @return
     * @throws IOException
     */
    @Override
    public ArrayList<Sample> parse(String filepath) throws IOException {
        /*TODO: I need to access the tree here I guess, but don't know yet how that is done
          Right now, an empty HashMap "treeStructure" is used
        */
        HashMap<Integer, TaxonNode> treeStructure = new HashMap<>(); //This should be a hashmap that maps taxon ids to TaxonNode objects
        HashMap<TaxonNode, Integer> node2CountMap = new HashMap<>(); //This one maps nodes to their read counts
        //Initialize all read counts with zero
        for (TaxonNode node : treeStructure.values()) {
            node2CountMap.put(node, 0);
        }

        //Initialize the object to be returned
        ArrayList<Sample> sampleList = new ArrayList<>();
        //This file format can only contain a single sample, so the list will contain only one element
        Sample sample = new Sample();
        sampleList.add(sample);
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line = reader.readLine();
        String[] lineSplit;
        int currentTaxonId;
        while (line != null) {
            lineSplit = line.split(",");
            currentTaxonId = Integer.parseInt(lineSplit[1]);
            //Get the TaxonNode for this sample
            TaxonNode currentTaxonNode = treeStructure.get(currentTaxonId);
            //Check if this taxon is already in the sample
            HashMap<TaxonNode, Integer> taxa2CountMap = sample.getTaxa2CountMap();
            if (taxa2CountMap.containsKey(currentTaxonNode)) {
                //Increment the count of this taxon in the sample by one
                taxa2CountMap.put(currentTaxonNode, taxa2CountMap.get(currentTaxonNode) + 1);
            } else {
                //Else add the taxon to the sample and set its count to one
                taxa2CountMap.put(currentTaxonNode, 1);
            }

            line = reader.readLine();
        }


        return sampleList;
    }
}
