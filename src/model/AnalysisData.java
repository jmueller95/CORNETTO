package model;

import analysis.GraphAnalysis;
import analysis.SampleComparison;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import mdsj.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalysisData {
    private static RealMatrix correlationMatrix, pValueMatrix;
    private static HashMap<TaxonNode, Double> maximumRelativeFrequencies;
    private static double highestFrequency;
    private static TaxonNode nodeWithHighestFrequency;
    private static int[] highestPositiveCorrelationCoordinates, highestNegativeCorrelationCoordinates;
    private static String level_of_analysis;
    //Possible values: "Domain", "Kingdom", "Phylum", "Class", "Order", "Family", "Genus", "Species"

    //Filter properties
    private static DoubleProperty negCorrelationLowerFilter = new SimpleDoubleProperty();
    private static DoubleProperty negCorrelationUpperFilter = new SimpleDoubleProperty();
    private static DoubleProperty posCorrelationLowerFilter = new SimpleDoubleProperty();
    private static DoubleProperty posCorrelationUpperFilter = new SimpleDoubleProperty();
    private static DoubleProperty maxPValueFilter = new SimpleDoubleProperty();
    private static DoubleProperty minFrequencyFilter = new SimpleDoubleProperty();
    private static DoubleProperty maxFrequencyFilter = new SimpleDoubleProperty();

    private static DoubleProperty excludeFrequencyThreshold = new SimpleDoubleProperty();


    //Graph analysis object
    private static GraphAnalysis analysis;

    /**
     * Receives a list of samples, calculates correlationMatrix and pValueMatrix for it
     *
     * @param samples
     */
    public static boolean performCorrelationAnalysis(ArrayList<Sample> samples, String type) {
        //Check if data is sufficient for analysis performing (check if there are at least two taxa)
        if (SampleComparison.getUnifiedTaxonList(samples, level_of_analysis).size() > 1) {
            maximumRelativeFrequencies = SampleComparison.calcMaximumRelativeFrequencies(samples, level_of_analysis);
            SampleComparison.createCorrelationOfSamples(samples, level_of_analysis, type);
            correlationMatrix = SampleComparison.getCorrelationMatrixOfSamples();
            pValueMatrix = SampleComparison.getCorrelationPValuesOfSamples();
            calcHighestFrequency();
            highestPositiveCorrelationCoordinates = calcHighestPositiveCorrelationCoordinates();
            highestNegativeCorrelationCoordinates = calcHighestNegativeCorrelationCoordinates();
            return true;
        } else {
            return false;
        }
    }

    public static RealMatrix getCorrelationMatrix() {
        return correlationMatrix;
    }


    public static RealMatrix getPValueMatrix() {
        return pValueMatrix;
    }


    /**
     * Calculates Multi Dimensional Scaling matrix of the correlations.
     * @return
     */
    public static double[][] getMDSMatrix() {

        System.out.println("Start MDS Matrix calculation");
        long now = System.currentTimeMillis();
        double[][] mdsj =  MDSJ.classicalScaling(correlationMatrix.getData());
        System.out.println("Finished. Duration: " + (now - System.currentTimeMillis()) + "ms" );
        return mdsj;
    }

    /**
     * Returns the coordinates in the correlation matrix with the highest positive value in the shape {x,y}
     *
     * @return
     */
    private static int[] calcHighestPositiveCorrelationCoordinates() {
        double max = -1;
        int[] maxCoordinates = {0, 0};
        for (int i = 0; i < correlationMatrix.getRowDimension(); i++) {
            for (int j = 0; j < correlationMatrix.getColumnDimension(); j++) {
                if (i != j && correlationMatrix.getEntry(i, j) > max) {
                    max = correlationMatrix.getEntry(i, j);
                    maxCoordinates[0] = i;
                    maxCoordinates[1] = j;
                }
            }
        }
        return maxCoordinates;
    }

    /**
     * Returns the coordinates in the correlation matrix with the highest negative value in the shape {x,y}
     *
     * @return
     */
    private static int[] calcHighestNegativeCorrelationCoordinates() {
        double min = 1;
        int[] minCoordinates = {0, 0};
        for (int i = 0; i < correlationMatrix.getRowDimension(); i++) {
            for (int j = 0; j < correlationMatrix.getColumnDimension(); j++) {
                if (i != j && correlationMatrix.getEntry(i, j) < min) {
                    min = correlationMatrix.getEntry(i, j);
                    minCoordinates[0] = i;
                    minCoordinates[1] = j;
                }
            }
        }
        return minCoordinates;

    }

    private static void calcHighestFrequency() {
        double max = 0;
        TaxonNode argMax = null;
        for (TaxonNode taxonNode : maximumRelativeFrequencies.keySet()) {
            if (maximumRelativeFrequencies.get(taxonNode) > max) {
                max = maximumRelativeFrequencies.get(taxonNode);
                argMax = taxonNode;
            }
        }
        highestFrequency = max;
        nodeWithHighestFrequency = argMax;
    }

    public static String getLevel_of_analysis() {
        return level_of_analysis;
    }

    public static void setLevel_of_analysis(String level_of_analysis) {
        AnalysisData.level_of_analysis = level_of_analysis;
    }


    public static double getNegCorrelationLowerFilter() {
        return negCorrelationLowerFilter.get();
    }

    public static DoubleProperty negCorrelationLowerFilterProperty() {
        return negCorrelationLowerFilter;
    }

    public static double getNegCorrelationUpperFilter() {
        return negCorrelationUpperFilter.get();
    }

    public static DoubleProperty negCorrelationUpperFilterProperty() {
        return negCorrelationUpperFilter;
    }

    public static double getPosCorrelationLowerFilter() {
        return posCorrelationLowerFilter.get();
    }

    public static DoubleProperty posCorrelationLowerFilterProperty() {
        return posCorrelationLowerFilter;
    }

    public static double getPosCorrelationUpperFilter() {
        return posCorrelationUpperFilter.get();
    }

    public static DoubleProperty posCorrelationUpperFilterProperty() {
        return posCorrelationUpperFilter;
    }

    public static double getMaxPValueFilter() {
        return maxPValueFilter.get();
    }

    public static DoubleProperty maxPValueProperty() {
        return maxPValueFilter;
    }

    public static double getMinFrequencyFilter() {
        return minFrequencyFilter.get();
    }

    public static DoubleProperty minFrequencyProperty() {
        return minFrequencyFilter;
    }

    public static double getMaxFrequencyFilter() {
        return maxFrequencyFilter.get();
    }

    public static DoubleProperty maxFrequencyProperty() {
        return maxFrequencyFilter;
    }

    public static HashMap<TaxonNode, Double> getMaximumRelativeFrequencies() {
        return maximumRelativeFrequencies;
    }

    public static int[] getHighestPositiveCorrelationCoordinates() {
        return highestPositiveCorrelationCoordinates;
    }

    public static int[] getHighestNegativeCorrelationCoordinates() {
        return highestNegativeCorrelationCoordinates;
    }

    public static double getHighestFrequency() {
        return highestFrequency;
    }

    public static TaxonNode getNodeWithHighestFrequency() {
        return nodeWithHighestFrequency;
    }

    public static GraphAnalysis getAnalysis() {
        return analysis;
    }

    public static void setAnalysis(GraphAnalysis newAnalysis) {
        analysis = newAnalysis;
    }

    public static DoubleProperty excludeFrequencyThresholdProperty() {
        return excludeFrequencyThreshold;
    }

    public static double getExcludeFrequencyThreshold() {
        return excludeFrequencyThreshold.get();
    }

    /**
     * Helper method for printing a matrix to the console
     *
     * @param matrix
     */
    public static void printMatrix(RealMatrix matrix) {
        for (int rowIndex = 0; rowIndex < matrix.getRowDimension(); rowIndex++) {
            for (int colIndex = 0; colIndex < matrix.getColumnDimension(); colIndex++) {
                System.out.printf("%.3f", matrix.getEntry(rowIndex, colIndex));
                System.out.print("\t");
            }
            System.out.println();
        }
    }


}
