package sampleParser;



import model.Sample;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;



/**
 * Created by julian on 15.05.17.
 */
public class BiomParser implements InputFile {
    @Override
    public ArrayList<Sample> parse(String filepath) {

        File inputFile = new File(filepath);
        System.out.println("File found? " + inputFile.exists());
        // TODO Add file not found / file not valid handling

        try {

            FileReader fileReader = new FileReader(inputFile);
            //JsonReader jsonReader = new JsonReader(fileReader);
            //Gson gson = new Gson();


            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(inputFile));
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
