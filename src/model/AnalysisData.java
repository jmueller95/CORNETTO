package model;

import analysis.SampleComparison;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;

public class AnalysisData {
    private static RealMatrix correlationMatrix, pValueMatrix;


    /**
     * Receives a list of samples, calculates correlationMatrix and pValueMatrix for it
     * @param samples
     */
    public void performAnalysis(ArrayList<Sample> samples){
        correlationMatrix = SampleComparison.getCorrelationMatrixOfSamples(samples);
        pValueMatrix = SampleComparison.getCorrelationPValuesOfSamples(samples);
    }

    public static RealMatrix getCorrelationMatrix() {
        return correlationMatrix;
    }

    public static void setCorrelationMatrix(RealMatrix correlationMatrix) {
        AnalysisData.correlationMatrix = correlationMatrix;
    }

    public static RealMatrix getpValueMatrix() {
        return pValueMatrix;
    }

    public static void setpValueMatrix(RealMatrix pValueMatrix) {
        AnalysisData.pValueMatrix = pValueMatrix;
    }
}
