package analysis;

import model.Sample;
import model.TaxonNode;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import java.util.*;

/**
 * Class for the comparison of samples
 * Created by julian on 10.06.17.
 */
public abstract class SampleComparison {
    private static PearsonsCorrelation pearsonsCorrelation;
    private static SpearmansCorrelation spearmansCorrelation;
    private static KendallsCorrelation kendallsCorrelation;
    private static RealMatrix correlationMatrix;
    private static RealMatrix pValueMatrix;


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
            return (Integer.compare(id1, id2));
        });

        return unifiedTaxonList;
    }

    public static void createCorrelationOfSamples(List<Sample> samples, String rank, String type) {
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


        switch (type) {
            case "pearson":
                pearsonsCorrelation = new PearsonsCorrelation(taxaCounts);
                correlationMatrix = pearsonsCorrelation.getCorrelationMatrix();
                pValueMatrix = pearsonsCorrelation.getCorrelationPValues();
                break;
            case "spearman":
                spearmansCorrelation = new SpearmansCorrelation(new BlockRealMatrix(taxaCounts));
                correlationMatrix = spearmansCorrelation.getCorrelationMatrix();
                pValueMatrix = spearmansCorrelation.getRankCorrelation().getCorrelationPValues();
                break;
            case "kendall":
                kendallsCorrelation = new KendallsCorrelation(taxaCounts);
                correlationMatrix = kendallsCorrelation.getCorrelationMatrix();
                pValueMatrix = new PearsonsCorrelation(taxaCounts).getCorrelationPValues(); //No p-values available for kendall's correlation!
                break;
        }

        //Correct the NaNs to 0.0s
        for (int i = 0; i < correlationMatrix.getRowDimension(); i++) {
            for (int j = 0; j < correlationMatrix.getColumnDimension(); j++) {
                if (Double.isNaN(correlationMatrix.getEntry(i, j)))
                    correlationMatrix.setEntry(i, j, 0.0);

            }
        }

        for (int i = 0; i < pValueMatrix.getRowDimension(); i++) {
            for (int j = 0; j < pValueMatrix.getColumnDimension(); j++) {
                if (Double.isNaN(pValueMatrix.getEntry(i, j)))
                    pValueMatrix.setEntry(i, j, 1.0);
            }
        }



    }

    public static RealMatrix getCorrelationMatrixOfSamples() {
        return correlationMatrix;
    }

    public static RealMatrix getCorrelationPValuesOfSamples() {
        return pValueMatrix;
    }

    /**
     * Given a sample and a rank to operate on, returns a hashmap of all the taxa with this rank contained in the sample
     * and their relative frequencies
     *
     * @param sample
     * @param rank
     * @return
     */
    private static HashMap<TaxonNode, Double> getRelativeFrequenciesForSample(Sample sample, String rank) {
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

    /**
     * Returns a map of average counts of every node in the given sample list on the given rank
     *
     * @param samples
     * @return
     */
    public static HashMap<TaxonNode, Double> calcAverageCounts(List<Sample> samples, String rank) {
        LinkedList<TaxonNode> taxonList = getUnifiedTaxonList(samples, rank);
        HashMap<TaxonNode, Double> averageCountMap = new HashMap<>();
        for (TaxonNode taxonNode : taxonList) {
            int sum = 0;
            for (Sample sample : samples) {
                sum += sample.getTaxonCountRecursive(taxonNode);
            }
            averageCountMap.put(taxonNode, sum / (double) samples.size());
        }
        return averageCountMap;
    }

}
