package model;

import java.util.HashMap;

/**
 * Created by jm on 15.05.17.
 */
public class Sample {
    private HashMap<TaxonNode, Integer> taxa2CountMap;
    private HashMap<String, String> metaData;

    // Empty constructor needed in CSV Parsers
    public Sample() {
        this.taxa2CountMap = new HashMap<>();
        this.metaData = new HashMap<>();
    }

    // Constructor with initialisation data
    public Sample(HashMap<TaxonNode, Integer> taxa2CountMap, HashMap<String, String> metaData) {
        this.taxa2CountMap = taxa2CountMap;
        this.metaData = metaData;
    }

    //Getters
    public HashMap<TaxonNode, Integer> getTaxa2CountMap() {
        return taxa2CountMap;
    }
    public HashMap<String, String> getMetaData() {
        return metaData;
    }
}
