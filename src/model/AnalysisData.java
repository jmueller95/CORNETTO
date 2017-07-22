package model;

import analysis.SampleComparison;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;

public class AnalysisData {
    private static RealMatrix correlationMatrix, pValueMatrix;
    private static String level_of_analysis;
    //Possible values: "Domain", "Kingdom", "Phylum", "Class", "Order", "Family", "Genus", "Species"

    //Filter properties
    private static DoubleProperty minCorrelation = new SimpleDoubleProperty();
    private static DoubleProperty maxCorrelation = new SimpleDoubleProperty();
    private static DoubleProperty maxPValue = new SimpleDoubleProperty();
    private static DoubleProperty minFrequency = new SimpleDoubleProperty();
    private static DoubleProperty maxFrequency = new SimpleDoubleProperty();

    /**
     * Receives a list of samples, calculates correlationMatrix and pValueMatrix for it
     * @param samples
     */
    public static boolean performCorrelationAnalysis(ArrayList<Sample> samples){
        //Check if data is sufficient for analysis performing (check if there are at least two taxa)
        if(SampleComparison.getUnifiedTaxonList(samples, level_of_analysis).size()>1) {
            correlationMatrix = SampleComparison.getCorrelationMatrixOfSamples(samples, level_of_analysis);
            pValueMatrix = SampleComparison.getCorrelationPValuesOfSamples(samples, level_of_analysis);
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

    //Getter
    public static String getLevel_of_analysis() {
        return level_of_analysis;
    }

    //Setter
    public static void setLevel_of_analysis(String level_of_analysis) { AnalysisData.level_of_analysis = level_of_analysis; }


    public static double getMinCorrelation() {
        return minCorrelation.get();
    }

    public static DoubleProperty minCorrelationProperty() {
        return minCorrelation;
    }

    public static double getMaxCorrelation() {
        return maxCorrelation.get();
    }

    public static DoubleProperty maxCorrelationProperty() {
        return maxCorrelation;
    }

    public static double getMaxPValue() {
        return maxPValue.get();
    }

    public static DoubleProperty maxPValueProperty() {
        return maxPValue;
    }

    public static double getMinFrequency() {
        return minFrequency.get();
    }

    public static DoubleProperty minFrequencyProperty() {
        return minFrequency;
    }

    public static double getMaxFrequency() {
        return maxFrequency.get();
    }

    public static DoubleProperty maxFrequencyProperty() {
        return maxFrequency;
    }
}
