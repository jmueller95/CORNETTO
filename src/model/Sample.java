package model;

import java.util.HashMap;

/**
 * Created by jm on 15.05.17.
 */
public class Sample {

    private HashMap<TaxonNode, Integer> taxa2CountMap;
    private static HashMap<String, String> metaData;
    private   static String sampleId;


    // Empty constructor needed in CSV Parsers
    public Sample() {
        this.taxa2CountMap = new HashMap<>();
        this.metaData = new HashMap<>();
        this.sampleId = new String();
    }

    // Constructor with initialisation data
    public Sample(HashMap<TaxonNode, Integer> taxa2CountMap, HashMap<String, String> metaData) {
        this.taxa2CountMap = taxa2CountMap;
        this.metaData = metaData;
        this.sampleId = sampleId;
    }

    /**
     * Sums up the counts of taxonNode and the counts of all its children (and their children and so on...)
     *
     * @param taxonNode
     * @return
     */
    public int getTaxonCountRecursive(TaxonNode taxonNode) {
        int recursiveSum = getTaxa2CountMap().getOrDefault(taxonNode, 0);
        for (TaxonNode child :
                taxonNode.getChildNodeList()) {
            recursiveSum += getTaxonCountRecursive(child);
        }
        return recursiveSum;
    }


        //Getters
        public HashMap<TaxonNode, Integer> getTaxa2CountMap () {
            return taxa2CountMap;
        }

        public HashMap<String, String> getMetaData () {
            return metaData;
        }

        public   static String getSampleId()
    {
        return sampleId;
    }

        //Setters
        public void setTaxa2CountMap(HashMap<TaxonNode, Integer> taxa2CountMap) {
            this.taxa2CountMap = taxa2CountMap;
        }

        public void setSampleId(String id) {
            sampleId = id;
        }
    }
