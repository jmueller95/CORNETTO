package treeParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by julian on 15.05.17.
 */
public class TreeParser {
    //Strings
    private String line = "";
    protected String fileNamesDmp = "./res/names.dmp", fileNodesDmp = "./res/nodes.dmp";

    //ArrayLists
    protected ArrayList<NamesDmp> namesDmps = new ArrayList<>();
    protected ArrayList<NodesDmp> nodesDmps = new ArrayList<>();

    protected BufferedReader reader;

    //System
    private final void printFileDoesNotExist() {
        System.err.println("The file does not exist.");
    }

    //Parser methods

    /**
     * parses the names.dmp file
     * creates the namesDmps
     * adds ONLY the Taxon that have a scientific name
     */
    public void readNamesDmpFile() {
        try {
            reader = new BufferedReader(new FileReader(fileNamesDmp));
            while ((line = reader.readLine()) != null) {
                String[] wholeLine = line.split("\\|");
                String[] wholeLineWithoutSpaces = removeBlanksFromArray(wholeLine);
                if (wholeLineWithoutSpaces[3].equals("scientificname")) {
                    int id = Integer.parseInt(wholeLineWithoutSpaces[0]);
                    String name = wholeLineWithoutSpaces[1];
                    namesDmps.add(new NamesDmp(id, name));
                }

            }
        } catch (IOException e) {
            printFileDoesNotExist();
        }
    }

    /**
     * parses the nodes.dmp file
     * builds the tree by cross checking with the namesDmps
     */
    public void readNodesDmpFile() {
        try {
            reader = new BufferedReader(new FileReader(fileNodesDmp));
            while ((line = reader.readLine()) != null) {
                String[] wholeLine = line.split("\\|");
                String[] wholeLineWithoutSpaces = removeBlanksFromArray(wholeLine);
                int id = Integer.parseInt(wholeLineWithoutSpaces[0]);
                int parentId = Integer.parseInt(wholeLineWithoutSpaces[1]);
                String rank = wholeLineWithoutSpaces[2];

                nodesDmps.add(new NodesDmp(id, parentId, rank));
            }
        } catch (IOException e) {
            printFileDoesNotExist();
        }
    }

    /**
     * Removes the entire white space from an array.
     *
     * @param array - String array
     * @return
     */
    public static String[] removeBlanksFromArray(String[] array) {
        String[] arrayWithoutBlanks = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            arrayWithoutBlanks[i] = array[i].replaceAll("\\s+", "");
        }

        return arrayWithoutBlanks;
    }


    //Getters

    public ArrayList<NamesDmp> getNamesDmps() {
        return namesDmps;
    }

    public ArrayList<NodesDmp> getNodesDmps() {
        return nodesDmps;
    }

    //Setters, Julian needs these for Testing reasons

    public void setFileNamesDmp(String fileNamesDmp) {
        this.fileNamesDmp = fileNamesDmp;
    }

    public void setFileNodesDmp(String fileNodesDmp) {
        this.fileNodesDmp = fileNodesDmp;
    }
}
