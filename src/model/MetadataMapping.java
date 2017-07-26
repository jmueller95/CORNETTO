package model;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by NantiaL on 02.07.2017.
 */
public class MetadataMapping extends Sample{

    //

    //HashMap<String,String> MT  = new HashMap<>();

    //HashMap<String, String> metadataObjectSet = new HashMap<>();

    //

    private static HashMap<String, String> getMT() {
        return getMetaData(); //gets MetaData from Sample Class
    }

    private static ArrayList<String> metaDataObject = new ArrayList<>();

    static  ArrayList<String>  MetadataParser(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();
        String[] arr = line.split(" "); // the fields are tab separated
       // int columns = arr.length; //number of files' columns


      while(line != null ) {

          getMT().put(arr[0], arr[1]);
          System.out.println(getMT() + "\t");

          arr = line.split(" ");

          switch (line = reader.readLine()) {
          }



      }

        return metaDataObject;
}

     public static void main(String[] args) throws IOException {
         //if each IDs exist only once
       // ArrayList<String> show =  MetadataParser("./res/testFiles/metadataFilesTest");

         //if an ID come twice
         // ArrayList<String> show =  MetadataParser("./res/testFiles/metadataFilesTestWithSameID");
     }

}


