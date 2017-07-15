package model;

import scala.util.parsing.combinator.testing.Str;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;


/**
 * Created by NantiaL on 02.07.2017.
 */
public class MetadataMapping {


    private static ArrayList<String> metaDataObject = new ArrayList<>();


    public  static ArrayList<String> MetadataParser(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line = reader.readLine();
        String[] arr = line.split("\t"); // the fields are tab separated
        int columns = arr.length-4; //number of files' columns
        //HashMap<HashSet,String> metaDataObject = new HashMap();


      while(line != null ) {

          for (int j = 1; j <= columns; j++) {

              //HashMap<String,String> metadataObjectSet = new HashMap<>();
              //metadataObjectSet.put(arr[3],arr[4]); //includes its "treatment"
              metaDataObject.add(arr[3]); //includes its "treatment"
              metaDataObject.add(arr[4]); //includes its "dob"

              arr = line.split("\t");
              j++;
              line = reader.readLine();
          }


      }
      //return metaDataObject;
        return metaDataObject;
}

     public static void main(String[] args) throws IOException {
       ArrayList<String> show =  MetadataParser("./res/testFiles/metadataFileTest");
       //HashMap<String,String> show =  MetadataParser("./res/testFiles/metadataFileTest");
       System.out.println(show);
    }

}


