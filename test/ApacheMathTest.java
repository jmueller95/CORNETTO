
import org.apache.commons.math3.stat.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by julian on 27.05.17.
 */
public class ApacheMathTest {

    /**
     * Creates an random double array, tests some descriptive statistics functions
     * @throws Exception
     */
    @Test
    public void testDescriptiveStatistics() throws Exception {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < 100; i++) {
            stats.addValue(Math.random());
        }
        //Test mean function: mean should be equal to sum divided by length
        assertEquals(stats.getSum()/stats.getN(), stats.getMean(),0.001);
        //Test variance and standard deviation: standard deviation should be equal to square root of variance
        assertEquals(Math.sqrt(stats.getVariance()),stats.getStandardDeviation(),0.001);
    }

    //TODO: Test covariance & correlation, try to do some hypothesis testing
}
