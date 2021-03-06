package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jm on 15.05.17.
 */
public class Sample implements Serializable {

    private HashMap<TaxonNode, Integer> fullTaxa2CountMap;
    private HashMap<TaxonNode, Integer> taxa2CountMap;
    private static HashMap<String, String> metaDataMap;
    private String sampleId;
    private String name;
    private String pathToFile;

    // Empty constructor needed in CSV Parsers
    public Sample() {
        this.taxa2CountMap = new HashMap<>();
        metaDataMap = new HashMap<>();
        this.sampleId = "";
    }

    // Constructor with initialisation data
    public Sample(HashMap<TaxonNode, Integer> taxa2CountMap, HashMap<String, String> metaData) {
        this.taxa2CountMap = taxa2CountMap;
        metaDataMap = metaData;
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

    /**
     * Deletes every taxa from the sample whose frequency is below the threshold
     */
    public void filterTaxaPrimary() {
        int countSum = 0;
        for (Integer integer : taxa2CountMap.values()) {
            countSum += integer;
        }
        if (fullTaxa2CountMap == null) {
            fullTaxa2CountMap = (HashMap<TaxonNode, Integer>) taxa2CountMap.clone();
        }
        HashMap<TaxonNode, Integer> filteredMap = new HashMap<>();
        for (Map.Entry<TaxonNode, Integer> entry : fullTaxa2CountMap.entrySet()) {
            if (entry.getValue() / (double) countSum > AnalysisData.getExcludeFrequencyThreshold())
                filteredMap.put(entry.getKey(), entry.getValue());
        }
        taxa2CountMap = filteredMap;

    }

    //Getters
    public HashMap<TaxonNode, Integer> getTaxa2CountMap() {
        return taxa2CountMap;
    }

    public static HashMap<String, String> getMetaData() {
        return metaDataMap;
    }

    public String getSampleId() {
        return sampleId;
    }

    public String getName() {
        return name;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    //Setters
    public void setTaxa2CountMap(HashMap<TaxonNode, Integer> taxa2CountMap) {
        this.taxa2CountMap = taxa2CountMap;
    }

    public void setSampleId(String id) {
        sampleId = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static HashMap<String, String> getMetaDataMap() {
        return metaDataMap;
    }

    public static void setMetaDataMap(HashMap<String, String> metaDataMap) {
        Sample.metaDataMap = metaDataMap;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }
}
