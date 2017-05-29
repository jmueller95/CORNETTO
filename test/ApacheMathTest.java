
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.junit.Test;


import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by julian on 27.05.17.
 */
public class ApacheMathTest {

    /**
     * Creates an random double array, tests some descriptive statistics functions
     *
     * @throws Exception
     */
    @Test
    public void testDescriptiveStatistics() throws Exception {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < 100; i++) {
            stats.addValue(Math.random());
        }

        System.out.println("Testing array: " + Arrays.toString(stats.getValues()));
        //Test mean function: mean should be equal to sum divided by length
        assertEquals(stats.getSum() / stats.getN(), stats.getMean(), 0.001);
        //Test variance and standard deviation: standard deviation should be equal to square root of variance
        assertEquals(Math.sqrt(stats.getVariance()), stats.getStandardDeviation(), 0.001);
    }


    /**
     * Creates two random double arrays, calculates covariance and different correlation coefficients from it
     * @throws Exception
     */
    @Test
    public void testCorrelation() throws Exception {
        double[] x = new double[10];
        double[] y = new double[10];
        for (int i = 0; i < x.length; i++) {
            x[i] = Math.random()*100;
            y[i] = Math.random()*100;
        }
        System.out.println("x: " + Arrays.toString(x));
        System.out.println("y: " + Arrays.toString(y));
        System.out.println("Covariance: " + new Covariance().covariance(x,y));
        System.out.println("Pearson's Correlation: " + new PearsonsCorrelation().correlation(x,y));
        System.out.println("Spearman's Correlation: " + new SpearmansCorrelation().correlation(x,y));
        System.out.println("Kendall's Correlation: " + new KendallsCorrelation().correlation(x,y));
    }

    /**
     *Generates random double array, applies t-test to it
     * @throws Exception
     */
    @Test
    public void testStatisticalTests() throws Exception {
        double[] x = new double[1000];
        for (int i = 0; i < x.length; i++) {
            x[i] = Math.random()*100+1;
        }
        System.out.println("Testing array: " + Arrays.toString(x));
        //One-sample t-test
        double mu = new DescriptiveStatistics(x).getMean();
        System.out.println("Mean: " + mu);
        System.out.println("t-statistic for true mean (50): " + TestUtils.t(50, x));
        System.out.println("p-value for true mean (50): " + TestUtils.tTest(50,x));
        System.out.println("t-statistic for false mean (75): " + TestUtils.t(75,x));
        System.out.println("p-value for false mean (75): " + TestUtils.tTest(75,x));


    }
}
