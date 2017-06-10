package analysis;

import model.Sample;
import model.TaxonNode;

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
     * Returns -1 if taxonNode can't be found in both of the samples
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



}
