package model;

import java.util.HashMap;

/**
 * Created by jm on 15.05.17.
 */
public class Sample {
    private HashMap<TaxonNode, Integer> taxa2CountMap = new HashMap<>();
    private HashMap<String, String> metaData = new HashMap<>();



    //Getters
    public HashMap<TaxonNode, Integer> getTaxa2CountMap() {
        return taxa2CountMap;
    }

    public HashMap<String, String> getMetaData() {
        return metaData;
    }
}
