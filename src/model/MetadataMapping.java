package model;


import com.sun.media.jfxmedia.MetadataParser;
//import scala.util.regexp.Base;

import java.io.*;
import java.util.ArrayList;


/**
 * Created by NantiaL on 02.07.2017.
 */
public class MetadataMapping {

    private static ArrayList<String> metaDataObject = new ArrayList<>();

    public static  ArrayList<String>  MetadataParser(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line = reader.readLine();
        String[] arr = line.split("\t"); // the fields are tab separated
        int columns = arr.length; //number of files' columns
        //HashMap<HashSet,String> metaDataObject = new HashMap();


      while(line != null ) {
          for (int j = 1; j < columns; j++) {

              //HashMap<String,String> metadataObjectSet = new HashMap<>();
              metaDataObject.add(arr[3] ); //includes its "treatment"
              metaDataObject.add(arr[4] + "\t\t"); //includes its "dob"

              arr = line.split("\t");
              j++;
              line = reader.readLine();
              System.out.println("" + metaDataObject);
          }

      }
        return metaDataObject;
}

     public static void main(String[] args) throws IOException {
        //if the same ID twice exists, then throw an error
        // ArrayList<String> show =  MetadataParser("./res/metadataFileTestwithSameID");

         //if each IDs exist only once
         ArrayList<String> show =  MetadataParser("./res/metadataFileTest");

    }

}


