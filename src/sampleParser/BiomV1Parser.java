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
 * <h1>The class implements the parser for BiomV1 files</h1>
 * <p>
 * The class is dependant on a TaxonTree to be available!
 * There are two different versions of the biom files. This ons only works for the version 1!
 * BiomV1 files are always provided in JSON format. There are sparse and rich formats, both work.
 * When done parsing the file a list of samples is returned.
 * </p>
 *
 * @see treeParser.TreeParser
 * @see Sample
 */
public class BiomV1Parser implements InputFile {

    private ArrayList<Sample> sampleList;
    private TaxonTree taxonTree;

    public BiomV1Parser(TaxonTree taxonTree) {
        this.taxonTree = taxonTree;
    }

    @Override
    /**
     *
     * @param filepath the file to parse
     * @return list of samples
     */
    public ArrayList<Sample> parse(String filepath) {
        sampleList = new ArrayList<>();


        File inputFile = new File(filepath);
        System.out.println("File found? " + inputFile.exists());
        // TODO Add file not found / file not valid handling

        try {
            // Open file and read as JSONObject
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new FileReader(inputFile));

            // Detect matrix type
            boolean matrixTypeIsSparse = obj.get("matrix_type").equals("sparse");
            //System.out.println("Matrix sparse? : " + matrixTypeIsSparse);
            int sparseMatrixIndex = 0;

            // ROW = OBSERVATIONS
            // COLUMNS = SAMPLES
            int nrow = ((JSONArray) obj.get("rows")).size();
            int ncol = ((JSONArray) obj.get("columns")).size();

            // Loop over all samples in the file (columns)
            for (int col = 0; col < ncol; col++) {
                System.out.println("Loading sample " + col);
                HashMap<TaxonNode, Integer> currentTaxCount = new HashMap<>();
                HashMap<String, String> metaData = new HashMap<>();

                // Access metadata. What is needed? What do we want to include?
                metaData.put("sample", (String)((JSONObject)obj.get("columns")).get("id"));
                for (Object o: (JSONArray)((JSONObject) obj.get("columns")).get("metadata")){
                    String metaKey = (String) o;
                    String metaValue = (String)((JSONObject)((JSONObject)obj.get("columns")).get("metadata")).get(metaKey);

                    metaData.put(metaKey, metaValue);
                }


                // Loop over rows (data)

                    for (int row = 0; row < nrow; row++) {
                        JSONObject currentRow =  (JSONObject)((JSONArray) obj.get("rows")).get(row);
                        // Extract observation counts for this taxon
                        int count = 0;

                        if (matrixTypeIsSparse) {
                            int rowValue = ((Double) ((JSONArray) ((JSONArray) obj.get("data")).get(sparseMatrixIndex)).get(0)).intValue();
                            int colValue = ((Double) ((JSONArray) ((JSONArray) obj.get("data")).get(sparseMatrixIndex)).get(1)).intValue();
                            int countValue = ((Double) ((JSONArray) ((JSONArray) obj.get("data")).get(sparseMatrixIndex)).get(2)).intValue();

                            if (rowValue == row && colValue == col) {
                                count = countValue;
                                sparseMatrixIndex++;
                            }

                        } else {

                            count = ((Double) ((JSONArray) ((JSONArray) obj.get("data")).get(col)).get(row)).intValue();
                            if (count > 0) {

                                int id = Integer.parseInt(currentRow.get("id").toString());
                                currentTaxCount.put(taxonTree.getNodeForID(id), count);
                            }
                        }

                        // Extract sampleID and TaxonNode
                        int id = Integer.parseInt(currentRow.get("id").toString());
                        try{
                            // Add observation to sample if taxonomy is found in tree
                            currentTaxCount.put(taxonTree.getNodeForID(id), count);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Taxonomy identifier " + id + " was not found in the tree");
                            System.out.println(e.getMessage());
                        }

                    }

                sampleList.add(new Sample(currentTaxCount, metaData));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sampleList;
    }

}
