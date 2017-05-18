package sampleParser;

import model.Sample;
import model.TaxonNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by julian on 15.05.17.
 */
public class TaxonId2CountCSVParser implements InputFile{



    //So far, every column is read and the corresponding number of sample objects is created.
    //TODO: In the final program, the user should be allowed to choose which columns are read
    @Override
    public ArrayList<Sample> parse(String filepath) throws IOException{
        ArrayList<Sample> sampleList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line = reader.readLine();
        String[] lineSplit = line.split(",");
        int numberOfDatasets = lineSplit.length-1; //First column is taxId, rest are counts i.e. datasets
        for (int i = 0; i < numberOfDatasets; i++) {
            sampleList.add(new Sample());
        }
        while (line!= null){
            lineSplit = line.split(",");
            int taxonId = Integer.parseInt(lineSplit[0]);

            /*TODO: I need to access the tree here I guess, but don't know yet how that is done
            * Right now, I'll create a dummy node here with all values set to null except taxon id*/
            TaxonNode taxonNode = new TaxonNode(null,taxonId,null,null,null);
            //int readCount = (int) Double.parseDouble(lineSplit[1]);
            //Add counts to datasets
            for (int i = 1; i <= numberOfDatasets; i++) {
                int currentSampleReadCount = (int) Double.parseDouble(lineSplit[i]);
                sampleList.get(i-1).getTaxa2CountMap().put(taxonNode,currentSampleReadCount);
            }
            line = reader.readLine();
        }

        return sampleList;
    }
}
