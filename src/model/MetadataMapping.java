package model;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zeth on 31.07.2017.
 * eventually we need to map the metadata fields to each other
 * using quiime for example
 * then we create the content for the dropdown menu
 */
public class MetadataMapping extends Sample {
    //we need to get the metadata from the samples
    //we need to map the metadata to the quiime file format
    private void getMetaDataFromSamples(ArrayList<Sample> listOfCurrentlyLoadedSamples) {
        ArrayList<HashMap<String, String>> listOfMetaData = new ArrayList<>();
        for (Sample sample : listOfCurrentlyLoadedSamples) {
            listOfMetaData.add(sample.getMetaDataMap());
        }

    }

}


