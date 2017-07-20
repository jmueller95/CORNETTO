package model;

import UI.MainStageController;
import analysis.SampleComparison;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;

public class AnalysisData {
    private static RealMatrix correlationMatrix, pValueMatrix;
    private static String level_of_analysis;
    //Possible values: "Domain", "Kingdom", "Phylum", "Class", "Order", "Family", "Genus", "Species"

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

}
