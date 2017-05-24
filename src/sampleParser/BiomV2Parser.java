package sampleParser;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;
import model.Sample;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * File Parser for BIOM V2.0 and V2.1 Files based on the hdf5 format
 * Created by caspar on 24.05.17.
 */
public class BiomV2Parser implements InputFile{


    @Override
    public ArrayList<Sample> parse(String filepath) throws IOException{


        IHDF5SimpleReader reader = HDF5Factory.openForReading(filepath);
        System.out.println(reader.getDataSetInformation(filepath));

        return null;
    }
}
