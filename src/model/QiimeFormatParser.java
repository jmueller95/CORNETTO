package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * parses the metadata provided in the qiime format
 * sampleID has to be the same as the filename
 * multi-sample csv files can have sampleIDs as a header (start with a #)
 * everything that follows in the header of the metadata format is the name of that metadata field
 */
public class QiimeFormatParser {
    /**
     * parses the quiime file format
     * we only require sampleID, Treatment and DOB
     * @param file
     * @return
     */
    private ArrayList<MetaData> parseQuiime(String file){
        ArrayList<MetaData> listOfMetaData = new ArrayList<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(file)))){
            while ((line = reader.readLine()) != null){
                String[] fileContent = line.split("\t"); //fields are tab seperated
                String sampleID = fileContent[0];
                String treatment = fileContent[3];
                int DOB = Integer.parseInt(fileContent[4]);
                MetaData metaData = new MetaData(sampleID, treatment, DOB);
                listOfMetaData.add(metaData);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return listOfMetaData;
    }

}
