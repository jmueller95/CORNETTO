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


    /**
     * Returns a list of all taxa contained in at least one of the samples, ordered by id
     * @param samples
     * @return
     */
    public static ArrayList<TaxonNode> getUnifiedTaxonList(List<Sample> samples){
        ArrayList <TaxonNode> unifiedTaxonList = new ArrayList<>();
        //Iterate over all samples, add every taxon that isn't there yet
        for (Sample sample : samples) {
            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                if(!unifiedTaxonList.contains(taxonNode)){
                    unifiedTaxonList.add(taxonNode);
                }
            }
        }
        //Sort the list
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
    public static PearsonsCorrelation getPearsonsCorrelationOfSamples(List<Sample> samples) {
        //We need the unified map to make sure the counts are properly aligned
        ArrayList<TaxonNode> taxonNodeList = getUnifiedTaxonList(samples);

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
        return new PearsonsCorrelation(taxaCounts);
    }



    public static RealMatrix getCorrelationMatrixOfSamples(List<Sample> samples) {
        PearsonsCorrelation sampleCorrelation = getPearsonsCorrelationOfSamples(samples);
        return sampleCorrelation.getCorrelationMatrix();
        //Right now, it's a 2x2-matrix which I don't understand.
    }

    public static RealMatrix getCorrelationPValuesOfSamples(List<Sample> samples) {
        PearsonsCorrelation sampleCorrelation = getPearsonsCorrelationOfSamples(samples);
        return sampleCorrelation.getCorrelationPValues();
    }

    /**
         * Filters the taxa contained in two samples. Returns a list of taxa that lie below/above the given
         * lower/upper correlation thresholds and below the given p-Value threshold
         *
         * @param samples
         * @param lowerCorrelationThreshold
         * @param upperCorrelationThreshold
         * @param pValueThreshold
         * @return
         */
    public static ArrayList<TaxonNode> filterSamples(List<Sample> samples,
                                                     double lowerCorrelationThreshold, double upperCorrelationThreshold,
                                                     double pValueThreshold) {

        //Get the unfiltered List of all taxons contained in either sample1 or sample2 and sort it by node id
        ArrayList<TaxonNode> unfilteredTaxonList = getUnifiedTaxonList(samples);
        ArrayList<TaxonNode> filteredTaxonList = new ArrayList<>();

        //Get correlation matrix and p-value matrix
        RealMatrix correlationMatrix = getCorrelationMatrixOfSamples(samples);
        RealMatrix correlationPValues = getCorrelationPValuesOfSamples(samples);
        //TODO: How exactly do we filter? For now, I simply check the first entry in the array, but in the end...
        //TODO  ...we should probably use the average or something
        for (int taxonIndex = 0; taxonIndex < unfilteredTaxonList.size(); taxonIndex++) {
            if(correlationMatrix.getEntry(taxonIndex,0)<upperCorrelationThreshold &&
                    correlationMatrix.getEntry(taxonIndex,0)>lowerCorrelationThreshold&&
                    correlationPValues.getEntry(taxonIndex,0)<pValueThreshold){
                filteredTaxonList.add(unfilteredTaxonList.get(taxonIndex));
            }
        }
        return filteredTaxonList;
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
