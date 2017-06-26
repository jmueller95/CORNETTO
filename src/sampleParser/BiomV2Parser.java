package sampleParser;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;
import model.Sample;
import model.TaxonTree;

import java.io.IOException;
import java.util.ArrayList;

/**
 * File Parser for BIOM V2.0 and V2.1 Files based on the hdf5 format
 * Created by caspar on 24.05.17.
 */
public class BiomV2Parser implements InputFile{

    private TaxonTree taxonTree;

    // Normal Constructor with TaxonTree
    public BiomV2Parser(TaxonTree taxonTree) {
        this.taxonTree = taxonTree;
    }

    @Override
    public ArrayList<Sample> parse(String filepath) throws IOException{
        ArrayList<Sample> sampleList = new ArrayList<>();

        IHDF5SimpleReader reader = HDF5Factory.openForReading(filepath);

        // Get number of Samples in File
        String[] sampleArray = reader.readStringArray("/sample/ids");
        // Loop over Samples
        for (String sample:sampleArray) {

            String[] observationArray = reader.readStringArray("/observation/ids");
            for (String observation:observationArray) {


            }
            Sample newSample = new Sample();
            sampleList.add(new Sample());

        }
        return sampleList;
    }
}
