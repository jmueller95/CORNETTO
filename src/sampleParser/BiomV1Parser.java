package sampleParser;



import model.Sample;
import model.TaxonNode;
import model.TaxonTree;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * File parser vor BIOM v1.0 files based on the JSON format
 * Creates a sample object with metadata and taxonID entry.
 * Automatically identifies sparse and rich formats
 *
 */
public class BiomV1Parser implements InputFile {

    private ArrayList<Sample> sampleList;
    private TaxonTree taxonTree;

    public BiomV1Parser(TaxonTree taxonTree) {
        this.sampleList = new ArrayList<>();
        this.taxonTree = taxonTree;
    }

    @Override
    /**
     * @param filepath the file to parse
     * @return list of samples
     */
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
            int sparseMatrixIndex = 0;

            // Loop over all samples in the file (columns)
            for (int col = 0; col < ((JSONArray) obj.get("columns")).size(); col++) {
                System.out.println("Loading sample " + col);
                HashMap<TaxonNode, Integer> currentTaxCount = new HashMap<>();
                HashMap<String, String> metaData = new HashMap<>();

                // Access metadata. What is needed? What do we want to include?
                // TODO

                // Loop over rows (data)
                for (int row = 0; row < ((JSONArray) obj.get("rows")).size(); row++) {
                    JSONObject currentRow =  (JSONObject)((JSONArray) obj.get("rows")).get(row);

                    // Extract observation counts for this taxon
                    int count = 0;
                    if (matrixTypeIsSparse) {

                        int rowValue = ((Double)((JSONArray)((JSONArray) obj.get("data")).get(sparseMatrixIndex)).get(0)).intValue();
                        int colValue = ((Double)((JSONArray)((JSONArray) obj.get("data")).get(sparseMatrixIndex)).get(1)).intValue();
                        int countValue = ((Double)((JSONArray)((JSONArray) obj.get("data")).get(sparseMatrixIndex)).get(2)).intValue();

                        if (rowValue == row && colValue == col) {
                            count = countValue;
                            sparseMatrixIndex++;
                        }
                    } else {
                        // Dense Matrix
                        count = ((Double)((JSONArray)((JSONArray) obj.get("data")).get(row)).get(col)).intValue();
                    }

                    // Extract sampleID and TaxonNode
                    String id = (String) currentRow.get("id");
                    try{
                        // Add observation to sample if taxonomy is found in tree
                        currentTaxCount.put(taxonTree.getNodeForID(id), count);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }

                }

                sampleList.add(new Sample(currentTaxCount, metaData));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
