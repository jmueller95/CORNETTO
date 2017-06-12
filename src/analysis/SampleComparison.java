package analysis;

import model.Sample;
import model.TaxonNode;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the comparison of TWO Sample objects.
 * Created by julian on 10.06.17.
 */
public class SampleComparison {
    private Sample firstSample, secondSample;

    public SampleComparison(Sample firstSample, Sample secondSample) {
        this.firstSample = firstSample;
        this.secondSample = secondSample;

    }

    /**
     * Subtracts the count of a given taxon in the secondSample from the corresponding count in the firstSample
     * Returns -1 if taxonNode can't be found in both of the samples (TODO: Maybe avoid this by intializing every node with count = 0)
     * TODO: Decide whether only count for this taxon or recursive count is used
     */
    public int taxonCountDifference(TaxonNode taxonNode) {
        if (firstSample.getTaxa2CountMap().containsKey(taxonNode) && secondSample.getTaxa2CountMap().containsKey(taxonNode)) {
            int countInFirstSample = firstSample.getTaxa2CountMap().get(taxonNode);
            int countInSecondSample = secondSample.getTaxa2CountMap().get(taxonNode);

            //Use this code if recursive count is wanted
            //int countInFirstSample = firstSample.getTaxonCountRecursive(taxonNode);
            //int countInSecondSample = secondSample.getTaxonCountRecursive(taxonNode);
            return countInFirstSample - countInSecondSample;
        } else {
            System.err.println("Error! Taxon isn't contained in both of the samples! Returning -1...");
            return -1;
        }
    }



    /**
     * Accepts a list of taxonNodes and a string that specifies which kind of correlation should be calculated (either
     * "pearson", "spearman", or "kendall").
     * Calculates the counts of these nodes in both of the samples and returns the pearson/spearman/kendall
     * correlation coefficient of these two double arrays.
     * TODO: Either handle nodes that don't appear in both samples or intialize EVERY node with zero when parsing
     *
     * @param nodesList
     * @param correlation_type
     * @return correlation_coefficient
     */
    public double calculateCorrelation(List<TaxonNode> nodesList, String correlation_type) {
        double[] countsInFirstSample = new double[nodesList.size()];
        double[] countsInSecondSample = new double[nodesList.size()];
        for (int i = 0; i < nodesList.size(); i++) {
            countsInFirstSample[i] = firstSample.getTaxonCountRecursive(nodesList.get(i));
            countsInSecondSample[i] = secondSample.getTaxonCountRecursive(nodesList.get(i));
        }
        double correlation_coefficient = 0;
        switch (correlation_type) {
            case "pearson":
                correlation_coefficient = new PearsonsCorrelation().correlation(countsInFirstSample, countsInSecondSample);
                break;
            case "spearman":
                correlation_coefficient = new SpearmansCorrelation().correlation(countsInFirstSample, countsInSecondSample);
                break;
            case "kendall":
                correlation_coefficient = new KendallsCorrelation().correlation(countsInFirstSample, countsInSecondSample);
        }
        return correlation_coefficient;
    }
}
