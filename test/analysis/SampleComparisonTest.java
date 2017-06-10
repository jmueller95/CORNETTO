package analysis;

import model.Sample;
import model.TaxonNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by julian on 10.06.17.
 */
public class SampleComparisonTest {
    private TaxonNode node1, node2, node3, node4;
    private Sample sample1, sample2;
    private SampleComparison sampleComparison;

    /**
     * Creates 4 taxon nodes, adds 3 to each sample such that each sample has a node the other one doesn't have
     * Then creates the SampleComparison object with the two samples
     * Afterwards the samples look like this:
     * id ||  sample1 |   sample2
     * 1  ||  10      |   -
     * 2  ||  52      |   10
     * 3  ||  0       |   10
     * 4  ||  -       |   4
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        node1 = new TaxonNode(1, null, 0);
        node2 = new TaxonNode(2, null, 0);
        node3 = new TaxonNode(3, null, 0);
        node4 = new TaxonNode(4, null, 0);

        sample1 = new Sample();
        sample1.getTaxa2CountMap().put(node1, 10);
        sample1.getTaxa2CountMap().put(node2, 52);
        sample1.getTaxa2CountMap().put(node3, 1);
        sample2 = new Sample();
        sample2.getTaxa2CountMap().put(node2, 10);
        sample2.getTaxa2CountMap().put(node3, 10);
        sample2.getTaxa2CountMap().put(node4, 4);
        sampleComparison = new SampleComparison(sample1, sample2);
    }

    @Test
    public void testCountDiff() throws Exception {
        assertEquals(42, sampleComparison.taxonCountDifference(node2));
        assertEquals(-9, sampleComparison.taxonCountDifference(node3));
        assertEquals(-1, sampleComparison.taxonCountDifference(node1));
    }

}