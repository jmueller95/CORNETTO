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
     * Returns a list of all taxa with the given rank contained in at least one of the samples, sorted by id
     *
     * @param samples
     * @return
     */
    public static LinkedList<TaxonNode> getUnifiedTaxonList(List<Sample> samples, String rank) {
        LinkedList<TaxonNode> unifiedTaxonList = new LinkedList<>();
        //Iterate over all samples, add every taxon that has the wanted rank and isn't there yet
        for (Sample sample : samples) {
            for (TaxonNode taxonNode : sample.getTaxa2CountMap().keySet()) {
                if (taxonNode.getRank().equals(rank) && !unifiedTaxonList.contains(taxonNode)) {
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
     * Takes a list of samples and returns the PearsonCorrelation object based on their counts on the given rank
     *
     * @param samples
     * @return
     */
    private static void createPearsonsCorrelationOfSamples(List<Sample> samples, String rank) {
        //We need the unified map to make sure the counts are properly aligned
        LinkedList<TaxonNode> taxonNodeList = getUnifiedTaxonList(samples, rank);

        //The matrix data needs to be double, since PearsonsCorrelation only takes double arrays
        double[][] taxaCounts = new double[samples.size()][taxonNodeList.size()];
        for (int sampleIndex = 0; sampleIndex < samples.size(); sampleIndex++) {
            double[] currentSampleCounts = new double[taxonNodeList.size()];
            for (int taxonIndex = 0; taxonIndex < currentSampleCounts.length; taxonIndex++) {
                currentSampleCounts[taxonIndex] = samples.get(sampleIndex).getTaxonCountRecursive(taxonNodeList.get(taxonIndex));
            }
            taxaCounts[sampleIndex] = currentSampleCounts;
        }

        //Now we can compute the correlation matrix
        sampleCorrelation = new PearsonsCorrelation(taxaCounts);
    }


    public static RealMatrix getCorrelationMatrixOfSamples(List<Sample> samples, String rank) {
        createPearsonsCorrelationOfSamples(samples, rank);
        RealMatrix correlationMatrix = sampleCorrelation.getCorrelationMatrix();
        for (int i = 0; i < correlationMatrix.getRowDimension(); i++) {
            for (int j = 0; j < correlationMatrix.getColumnDimension(); j++) {
                if (Double.isNaN(correlationMatrix.getEntry(i, j)))
                    correlationMatrix.setEntry(i, j, 0.0);

            }
        }
        return correlationMatrix;
    }

    public static RealMatrix getCorrelationPValuesOfSamples(List<Sample> samples, String rank) {
        createPearsonsCorrelationOfSamples(samples, rank);

        RealMatrix correlationPValues = sampleCorrelation.getCorrelationPValues();
        for (int i = 0; i < correlationPValues.getRowDimension(); i++) {
            for (int j = 0; j < correlationPValues.getColumnDimension(); j++) {
                if (Double.isNaN(correlationPValues.getEntry(i, j)))
                    correlationPValues.setEntry(i, j, 1.0);
            }
        }
        return correlationPValues;
    }

    /**
     * Given a sample and a rank to operate on, returns a hashmap of all the taxa with this rank contained in the sample
     * and their relative frequencies
     *
     * @param sample
     * @param rank
     * @return
     */
    public static HashMap<TaxonNode, Double> getRelativeFrequenciesForSample(Sample sample, String rank) {
        //Get all taxa on the given rank
        LinkedList<TaxonNode> nodesOnRank = getUnifiedTaxonList(Collections.singletonList(sample), rank);
        int countSum = 0;
        HashMap<TaxonNode, Double> relativeCountsMap = new HashMap<>();
        for (TaxonNode taxonNode : nodesOnRank) {
            int taxonCount = sample.getTaxonCountRecursive(taxonNode);
            countSum += taxonCount;
            relativeCountsMap.put(taxonNode, (double) taxonCount);
        }
        //Replace absolute counts with relative counts
        for (TaxonNode taxonNode : nodesOnRank) {
            relativeCountsMap.put(taxonNode, relativeCountsMap.get(taxonNode) / countSum);
        }

        return relativeCountsMap;

    }

    /**
     * Given a list of samples and a rank to operate on, creates a mapping of all taxon nodes to their maximal relative
     * frequency. This method is needed for filtering (e.g. if we set "minimal frequency" to 0.3, only the taxa who appear
     * in at least one of the samples with a frequency of at least 0.3 will be shown)
     *
     * @param samples
     * @param rank
     * @return
     */
    public static HashMap<TaxonNode, Double> calcMaximumRelativeFrequencies(List<Sample> samples, String rank) {
        HashMap<Sample, HashMap<TaxonNode, Double>> allRelativeCounts = new HashMap<>();
        for (Sample sample : samples) {
            allRelativeCounts.put(sample, getRelativeFrequenciesForSample(sample, rank));
        }

        HashMap<TaxonNode, Double> maximumRelativeCountsMap = new HashMap<>();
        for (TaxonNode taxonNode : getUnifiedTaxonList(samples, rank)) {
            double maxRelativeCount = 0;
            for (Sample sample : samples) {
                double currentRelativeCount = allRelativeCounts.get(sample).getOrDefault(taxonNode, 0.d);
                maxRelativeCount = Math.max(maxRelativeCount, currentRelativeCount);
            }
            maximumRelativeCountsMap.put(taxonNode, maxRelativeCount);
        }
        return maximumRelativeCountsMap;
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
