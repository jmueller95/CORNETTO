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
     * Before comparing two samples, one has to make sure that their taxon counts are aligned.
     * Since they may not contain the same taxa, this can't be assumed in the first place.
     * This method returns a unification of the taxa2CountMaps of two samples where counts of non-existent taxa
     * are set to 0.
     *
     * @param sample1
     * @param sample2
     * @return
     */
    public static HashMap<TaxonNode, int[]> getUnifiedTaxa2CountMap(Sample sample1, Sample sample2) {
        HashMap<TaxonNode, int[]> unifiedTaxa2countMap = new HashMap<>();
        //First, add all taxa of sample1, try to find them in sample 2 (set count to 0 if they're not there)
        for (TaxonNode taxonNode : sample1.getTaxa2CountMap().keySet()) {
            int[] taxonCounts = new int[2];
            taxonCounts[0] = sample1.getTaxonCountRecursive(taxonNode);
            taxonCounts[1] = sample2.getTaxonCountRecursive(taxonNode);
            unifiedTaxa2countMap.put(taxonNode, taxonCounts);
        }
        //Second, add all taxa of sample2 that aren't already in the list
        for (TaxonNode taxonNode : sample2.getTaxa2CountMap().keySet()) {
            if (!unifiedTaxa2countMap.containsKey(taxonNode)) {
                int[] taxonCounts = new int[2];
                taxonCounts[0] = sample1.getTaxonCountRecursive(taxonNode);
                taxonCounts[1] = sample2.getTaxonCountRecursive(taxonNode);
                unifiedTaxa2countMap.put(taxonNode, taxonCounts);
            }
        }
        return unifiedTaxa2countMap;
    }

    /**
     * Takes two samples and returns the PearsonCorrelation object based on their counts
     *
     * @param sample1
     * @param sample2
     * @return
     */
    public static PearsonsCorrelation getPearsonsCorrelationForSamples(Sample sample1, Sample sample2) {
        //We need the unified map to make sure the counts are properly aligned
        HashMap<TaxonNode, int[]> unifiedTaxa2countMap = getUnifiedTaxa2CountMap(sample1, sample2);
        //Sort the TaxonNodes by id
        ArrayList<TaxonNode> taxonNodeList = new ArrayList<>(unifiedTaxa2countMap.keySet());
        taxonNodeList.sort((tn1, tn2) -> {
            int id1 = tn1.getTaxonId();
            int id2 = tn2.getTaxonId();
            return (id1 > id2 ? -1 : (id1 == id2 ? 0 : 1));
        });

        //The matrix data needs to be double, since PearsonsCorrelation only takes double arrays
        double[][] taxaCounts = new double[unifiedTaxa2countMap.size()][2];
        int counter = 0;
        for (TaxonNode taxonNode : taxonNodeList) {
            int[] currentCounts = unifiedTaxa2countMap.get(taxonNode);
            taxaCounts[counter][0] = (double) currentCounts[0];
            taxaCounts[counter][1] = (double) currentCounts[1];
            counter++;
        }
        //Now we can compute the correlation matrix
        return new PearsonsCorrelation(taxaCounts);
    }

    public static RealMatrix getCorrelationMatrixForSamples(Sample sample1, Sample sample2) {
        PearsonsCorrelation sampleCorrelation = getPearsonsCorrelationForSamples(sample1, sample2);
        return sampleCorrelation.getCorrelationMatrix();
        //TODO: This is not the matrix we want - it should be an nXn matrix comparing every taxon to every other taxon (I guess)
        //Right now, it's a 2x2-matrix.
    }

    public static RealMatrix getCorrelationPValuesForSamples(Sample sample1, Sample sample2) {
        PearsonsCorrelation sampleCorrelation = getPearsonsCorrelationForSamples(sample1, sample2);
        return sampleCorrelation.getCorrelationPValues();
    }

    /**
     * Filters the taxa contained in two samples. Returns a list of taxa that lie below/above the given
     * lower/upper correlation thresholds and below the given p-Value threshold
     * @param sample1
     * @param sample2
     * @param lowerCorrelationThreshold
     * @param upperCorrelationThreshold
     * @param pValueThreshold
     * @return
     */
    public static ArrayList<TaxonNode> filterSamples(Sample sample1, Sample sample2,
                                                     double lowerCorrelationThreshold, double upperCorrelationThreshold,
                                                     double pValueThreshold) {

        //Get the unfiltered List of all taxons contained in either sample1 or sample2 and sort it by node id
        ArrayList<TaxonNode> unfilteredTaxonList = new ArrayList<>();
        unfilteredTaxonList.addAll(getUnifiedTaxa2CountMap(sample1, sample2).keySet());
        unfilteredTaxonList.sort((tn1, tn2) -> {
            int id1 = tn1.getTaxonId();
            int id2 = tn2.getTaxonId();
            return (id1 > id2 ? -1 : (id1 == id2 ? 0 : 1));
        });
        //Calculate correlation and p-value matrices
        RealMatrix samplesCorrelationMatrix = getCorrelationMatrixForSamples(sample1,sample2);
        RealMatrix samplesCorrelationPValueMatrix = getCorrelationPValuesForSamples(sample1,sample2);
        ArrayList<TaxonNode> filteredTaxonList = new ArrayList<>();
        //TODO: How exactly do we filter? --> How does the matrix look like?
        return filteredTaxonList;
    }

}
