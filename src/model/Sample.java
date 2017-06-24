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

    /**
     * Sums up the counts of taxonNode and the counts of all its children (and their children and so on...)
     *
     * @param taxonNode
     * @return
     */
    public int getTaxonCountRecursive(TaxonNode taxonNode) {
        if (getTaxa2CountMap().containsKey(taxonNode)) {
            if (!taxonNode.isLeaf()) {
                int childrenSum = getTaxa2CountMap().get(taxonNode);
                for (TaxonNode child :
                        taxonNode.getChildNodeList()) {
                    childrenSum += getTaxonCountRecursive(child);
                }
                return childrenSum;

            } else {
                return getTaxa2CountMap().get(taxonNode);
            }
        } else {
            return 0;
        }
    }


    //Getters
    public HashMap<TaxonNode, Integer> getTaxa2CountMap() {
        return taxa2CountMap;
    }

    public HashMap<String, String> getMetaData() {
        return metaData;
    }
}
