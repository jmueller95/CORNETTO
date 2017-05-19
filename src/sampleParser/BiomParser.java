package sampleParser;



import model.Sample;
import model.TaxonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * File parser vor BIOM v1.0 files based on the JSON format
 * Creates a sample object with metadata and taxonID entry.
 * Automatically identifies sparse and rich formats
 *
 */
public class BiomParser implements InputFile {

    @Override
    public ArrayList<Sample> parse(String filepath) {

        File inputFile = new File(filepath);
        System.out.println("File found? " + inputFile.exists());
        // TODO Add file not found / file not valid handling

        try {
            // Open file and read as JSONObject
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new FileReader(inputFile));

            // Detect matrix type
            boolean matrixTypeIsSparse = obj.get("matrix_type").equals("sparse");
            System.out.println("Matrix sparse? : " + matrixTypeIsSparse);

            // Loop over all samples in the file (columns)
            for (int col = 0; col < ((JSONArray) obj.get("columns")).size(); col++) {
                System.out.println("Loading sample " + col);
                HashMap<TaxonNode, Integer> currentSample = new HashMap<>();

                // Access metadata. What is needed? What do we want to include?
                // TODO

                // Loop over rows (data)
                for (int row = 0; row < ((JSONArray) obj.get("rows")).size(); row++) {
                    JSONObject currentRow =  (JSONObject)((JSONArray) obj.get("rows")).get(row);

                    // Extract taxon/sampleID
                    String id = (String) currentRow.get("id");
                    //TODO get taxon for this ID from TaxonTree


                    // Extract observation counts for this taxon
                    double count = 0;
                    if (matrixTypeIsSparse) {
                        System.out.println("Sparse matrix type not supported yet");
                    } else {
                        count = (double)((JSONArray)((JSONArray) obj.get("data")).get(row)).get(0);
                    }
                    // First step: Print to console
                    System.out.println(id + " " + count);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
