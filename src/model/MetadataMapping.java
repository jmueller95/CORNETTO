package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NantiaL on 02.07.2017.
 */
public class MetadataMapping {


    Sample SampleID = new Sample();
    Sample MetadataMap = new Sample();
    private ArrayList<Sample> sampleList;


    //Getters

    public Sample getMetadataMap() {
        return MetadataMap;
    }

    //empty constructor
    public MetadataMapping() {
        this.SampleID = new Sample();
        this.MetadataMap = new Sample();
    }

    // Constructor with data
    public MetadataMapping(Sample SampleID, String header, String barcodeSequence) {
        this.SampleID = SampleID;
        this.MetadataMap = MetadataMap;
    }

    public ArrayList<Sample> MetadataParser(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line = reader.readLine();
        String[] split = line.split(" ");
        int id = split.length - 1; //shows that the first column is the sample ID


      //  int numberOfData = split.length - 1;
      //  HashMap<String, String> map = getMetadataMap().getMetaData();



        return sampleList;
    }

}


