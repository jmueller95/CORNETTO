package model;

/**
 * Created by NantiaL on 02.07.2017.
 */
public class MetadataMapping {
    package model;

import com.sun.xml.internal.fastinfoset.util.StringArray;

import java.util.HashMap;

    /**
     * Created by NantiaL on 30.06.2017.
     */


        Sample SampleID = new Sample();
        private static String barcodeSequence ;
        private static String header;

        //Getters
        public static String getBarcodeSequence() {
            return barcodeSequence;
        }

        public static String getHeader() {
            return header;
        }

        public Sample getSampleID() {
            return SampleID;
        }

        //empty constructor
        public MetadataMapping() {
            this.barcodeSequence = new String();
            this.header = new String();
            this.SampleID = new Sample();
        }

        // Constructor with data
        public MetadataMapping(Sample SampleID, String header,String barcodeSequence) {
            this.SampleID = SampleID;
            this.header = header;
            this.barcodeSequence = barcodeSequence;
        }







    }


