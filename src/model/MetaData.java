package model;

/**
 * used for the quiime parser, defines MetaData
 */
public class MetaData {
    private String sampleID;
    private String treatment;
    private int DOB;

    /**
     * Constructor
     * @param sampleID
     * @param treatment
     * @param DOB
     */
    public MetaData(String sampleID, String treatment, int DOB) {
        this.sampleID = sampleID;
        this.treatment = treatment;
        this.DOB = DOB;
    }
}
