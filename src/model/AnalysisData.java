package model;

import analysis.SampleComparison;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.HashMap;

public class AnalysisData {
    private static RealMatrix correlationMatrix, pValueMatrix;
    private static HashMap<TaxonNode, Double> maximumRelativeFrequencies;
    private static double[] highestPositiveCorrelationCoordinates, highestNegativeCorrelationCoordinates;
    private static String level_of_analysis;
    //Possible values: "Domain", "Kingdom", "Phylum", "Class", "Order", "Family", "Genus", "Species"

    //Filter properties
    private static DoubleProperty minCorrelationFilter = new SimpleDoubleProperty();
    private static DoubleProperty maxCorrelationFilter = new SimpleDoubleProperty();
    private static DoubleProperty maxPValueFilter = new SimpleDoubleProperty();
    private static DoubleProperty minFrequencyFilter = new SimpleDoubleProperty();
    private static DoubleProperty maxFrequencyFilter = new SimpleDoubleProperty();

    /**
     * Receives a list of samples, calculates correlationMatrix and pValueMatrix for it
     * @param samples
     */
    public static boolean performCorrelationAnalysis(ArrayList<Sample> samples){
        //Check if data is sufficient for analysis performing (check if there are at least two taxa)
        if(SampleComparison.getUnifiedTaxonList(samples, level_of_analysis).size()>1) {
            correlationMatrix = SampleComparison.getCorrelationMatrixOfSamples(samples, level_of_analysis);
            pValueMatrix = SampleComparison.getCorrelationPValuesOfSamples(samples, level_of_analysis);
            maximumRelativeFrequencies = SampleComparison.calcMaximumRelativeFrequencies(samples, level_of_analysis);
            highestPositiveCorrelationCoordinates = calcHighestPositiveCorrelationCoordinates();
            highestNegativeCorrelationCoordinates = calcHighestNegativeCorrelationCoordinates();
            return true;
        }else{
           return false;
        }
    }

    public static RealMatrix getCorrelationMatrix() {
        return correlationMatrix;
    }

    public static void setCorrelationMatrix(RealMatrix correlationMatrix) {
        AnalysisData.correlationMatrix = correlationMatrix;
    }

    public static RealMatrix getPValueMatrix() {
        return pValueMatrix;
    }

    public static void setPValueMatrix(RealMatrix pValueMatrix) {
        AnalysisData.pValueMatrix = pValueMatrix;
    }


    /**
     * Returns the coordinates in the correlation matrix with the highest positive value in the shape {x,y}
     * @return
     */
    private static double[] calcHighestPositiveCorrelationCoordinates() {
        double max = -1;
        double[] maxCoordinates = {0,0};
        for (int i = 0; i < correlationMatrix.getRowDimension(); i++) {
            for (int j = 0; j < correlationMatrix.getColumnDimension(); j++) {
                if(correlationMatrix.getEntry(i,j)>max){
                    max = correlationMatrix.getEntry(i,j);
                    maxCoordinates[0] = i;
                    maxCoordinates[1] = j;
                }
            }
        }
        return maxCoordinates;
    }

    /**
     * Returns the coordinates in the correlation matrix with the highest negative value in the shape {x,y}
     * @return
     */
    private static double[] calcHighestNegativeCorrelationCoordinates() {
        double min = 1;
        double[] minCoordinates = {0,0};
        for (int i = 0; i < correlationMatrix.getRowDimension(); i++) {
            for (int j = 0; j < correlationMatrix.getColumnDimension(); j++) {
                if(correlationMatrix.getEntry(i,j)<min){
                    min = correlationMatrix.getEntry(i,j);
                    minCoordinates[0] = i;
                    minCoordinates[1] = j;
                }
            }
        }
        return minCoordinates;

    }

    public static String getLevel_of_analysis() {
        return level_of_analysis;
    }

    public static void setLevel_of_analysis(String level_of_analysis) { AnalysisData.level_of_analysis = level_of_analysis; }

    public static double getMinCorrelationFilter() {
        return minCorrelationFilter.get();
    }

    public static DoubleProperty minCorrelationProperty() {
        return minCorrelationFilter;
    }

    public static double getMaxCorrelationFilter() {
        return maxCorrelationFilter.get();
    }

    public static DoubleProperty maxCorrelationProperty() {
        return maxCorrelationFilter;
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

    public static double[] getHighestPositiveCorrelationCoordinates() {
        return highestPositiveCorrelationCoordinates;
    }

    public static double[] getHighestNegativeCorrelationCoordinates() {
        return highestNegativeCorrelationCoordinates;
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
