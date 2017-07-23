package model;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//import scala.util.regexp.Base;


/**
 * Created by NantiaL on 02.07.2017.
 */
public class MetadataMapping {

    private static ArrayList<String> metaDataObject = new ArrayList<>();

    public static  ArrayList<String>  MetadataParser(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String line = reader.readLine();
        String[] arr = line.split(" "); // the fields are tab separated
        int columns = arr.length; //number of files' columns
        //HashMap<HashSet,String> metaDataObject = new HashMap();


      while(line != null ) {
/*
              //HashMap<String,String> metadataObjectSet = new HashMap<>();
              metaDataObject.add(arr[0]);
              metaDataObject.add(arr[3] ); //includes its "treatment"
              metaDataObject.add(arr[4] + "\t\t"); //includes its "dob"

              arr = line.split(" ");

              line = reader.readLine();
              System.out.println("" + metaDataObject);
*/


       HashMap<String, String> metadataObjectSet = new HashMap<>();
       metadataObjectSet.put(arr[0], arr[3]);
       System.out.println(metadataObjectSet + "\t");


          arr = line.split(" ");

          line = reader.readLine();



      }

        return metaDataObject;
}

     public static void main(String[] args) throws IOException {
         //if each IDs exist only once
        ArrayList<String> show =  MetadataParser("./res/testFiles/metadataFilesTest");



    }

}


