package analysis;

import model.Sample;
import model.TaxonNode;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.util.*;

/**
 * Class for the comparison of samples
 * Created by julian on 10.06.17.
 */
public abstract class SampleComparison {
    protected static PearsonsCorrelation sampleCorrelation;


    /**
     * Returns a list of all taxa contained in at least one of the samples, ordered by id
     *
     * @param samples
     * @return
     */
    public static LinkedList<TaxonNode> getUnifiedTaxonList(List<Sample> samples) {
        LinkedList<TaxonNode> unifiedTaxonList = new LinkedList<>();
        //Iterate over all samples, add every taxon that isn't there yet
        for (Sample sample : samples) {
            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                if (!unifiedTaxonList.contains(taxonNode)) {
                    unifiedTaxonList.add(taxonNode);
                }
            }
        }
        //Sort the list TODO: Might it be more efficient to directly insert the nodes sorted?
        unifiedTaxonList.sort((tn1, tn2) -> {
            int id1 = tn1.getTaxonId();
            int id2 = tn2.getTaxonId();
            return (id1 > id2 ? 1 : (id1 == id2 ? 0 : -1));
        });

        return unifiedTaxonList;
    }


    /**
     * Takes a list of samples and returns the PearsonCorrelation object based on their counts
     *
     * @param samples
     * @return
     */
    private static void createPearsonsCorrelationOfSamples(List<Sample> samples) {
        //We need the unified map to make sure the counts are properly aligned
        LinkedList<TaxonNode> taxonNodeList = getUnifiedTaxonList(samples);

        //The matrix data needs to be double, since PearsonsCorrelation only takes double arrays
        double[][] taxaCounts = new double[samples.size()][taxonNodeList.size()];
        for (int sampleIndex = 0; sampleIndex < samples.size(); sampleIndex++) {
            double[] currentSampleCounts = new double[taxonNodeList.size()];
            for (int taxonIndex = 0; taxonIndex < currentSampleCounts.length; taxonIndex++) {
                currentSampleCounts[taxonIndex] = samples.get(sampleIndex).getTaxa2CountMap().get(taxonNodeList.get(taxonIndex));
            }
            taxaCounts[sampleIndex] = currentSampleCounts;
        }

        //Now we can compute the correlation matrix
        sampleCorrelation = new PearsonsCorrelation(taxaCounts);
    }


    public static RealMatrix getCorrelationMatrixOfSamples(List<Sample> samples) {
        if (sampleCorrelation == null) {
            createPearsonsCorrelationOfSamples(samples);
        }
        return sampleCorrelation.getCorrelationMatrix();
    }

    public static RealMatrix getCorrelationPValuesOfSamples(List<Sample> samples) {
        if (sampleCorrelation == null) {
            createPearsonsCorrelationOfSamples(samples);
        }
        return sampleCorrelation.getCorrelationPValues();
    }


/*
PROBABLY DEPRECATED - Does the same as "getCorrelationMatrixOfSamples", but returns a hashmap so you know which nodes
these fancy numbers belong to.
 */
//    /**
//     * @param samples
//     * @return
//     */
//    public static HashMap<TaxonNode, HashMap<TaxonNode, Double>> getPairwiseCorrelations(List<Sample> samples) {
//        ArrayList<TaxonNode> unifiedTaxonList = getUnifiedTaxonList(samples);
//        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
//        HashMap<TaxonNode, HashMap<TaxonNode, Double>> pairwiseCorrelationMap = new HashMap<>();
//
//        for (TaxonNode currentTaxonNode : unifiedTaxonList) {
//            HashMap<TaxonNode, Double> currentNodeCorrelationMap = new HashMap<>();
//            for (TaxonNode otherTaxonNode : unifiedTaxonList) {
//                double[] currentCounts = new double[samples.size()];
//                double[] otherCounts = new double[samples.size()];
//                for (int i = 0; i < currentCounts.length; i++) {
//                    currentCounts[i] = samples.get(i).getTaxa2CountMap().get(currentTaxonNode);
//                    otherCounts[i] = samples.get(i).getTaxa2CountMap().get(otherTaxonNode);
//                }
//                double currentCorrelation = pearsonsCorrelation.correlation(currentCounts,otherCounts);
//                currentNodeCorrelationMap.put(otherTaxonNode,currentCorrelation);
//            }
//            pairwiseCorrelationMap.put(currentTaxonNode,currentNodeCorrelationMap);
//        }
//    return pairwiseCorrelationMap;
//    }

}
