package treeParser;

import model.TaxonNode;
import model.TaxonTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by julian on 15.05.17.
 */
public class TreeParser {
    //Tree to be created
    private TaxonTree taxonTree;

    public TreeParser() {
        this.taxonTree = new TaxonTree();
    }

    //System
    private final void printFileDoesNotExist() {
        System.err.println("The file does not exist.");
    }

    //Parser methods
    public void parseTree(String fileNodesDmp, String fileNamesDmp) {
        readNodesDmpFile(fileNodesDmp);
        readNamesDmpFile(fileNamesDmp);
    }


    /**
     * parses the nodes.dmp file
     * builds the tree (without names!)
     */
    public void readNodesDmpFile(String fileNodesDmp) {
        /*DEBUG*/ long startTime = System.currentTimeMillis();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileNodesDmp));

            //First, read and create the root
            String line = reader.readLine();
            String[] lineSplit = line.split("\\|");
            String[] lineSplitWithoutSpaces = removeBlanksFromArray(lineSplit);
            int id = Integer.parseInt(lineSplitWithoutSpaces[0]);
            int parentId = Integer.parseInt(lineSplitWithoutSpaces[1]); //Should also be 1 for the root
            String rank = lineSplitWithoutSpaces[2];
            TaxonNode currentNode = new TaxonNode(id, rank, parentId);
            taxonTree.setRoot(currentNode);
            taxonTree.getTreeStructure().put(id, currentNode);

            //Parse the rest of the tree
            while ((line = reader.readLine()) != null) {
                lineSplit = line.split("\\|");
                lineSplitWithoutSpaces = removeBlanksFromArray(lineSplit);
                id = Integer.parseInt(lineSplitWithoutSpaces[0]);
                parentId = Integer.parseInt(lineSplitWithoutSpaces[1]);
                rank = lineSplitWithoutSpaces[2];
                //Check if node already exists (happens if it's the parent of a previously parsed node)
                if (taxonTree.getTreeStructure().containsKey(id)) {
                    //In this case only update the existing node (it only contains an id and a child list so far)
                    currentNode = taxonTree.getTreeStructure().get(id);
                    currentNode.setParentId(parentId);
                    currentNode.setRank(rank);
                } else {
                    //Create a new node and add it to the tree
                    currentNode = new TaxonNode(id, rank, parentId);
                    taxonTree.getTreeStructure().put(id, currentNode);
                }

                //Check if parent node doesn't exist yet (happens if parentId>id)
                TaxonNode parentNode;
                if (parentId > id) {
                    parentNode = new TaxonNode(parentId, currentNode);
                } else {
                    parentNode = taxonTree.getTreeStructure().get(parentId);
                    parentNode.getChildNodeList().add(currentNode);
                }
                taxonTree.getTreeStructure().put(parentId,parentNode);
                //Set parentNode as parent of currentNode
                currentNode.setParentNode(parentNode);
            }
        } catch (IOException e) {
            printFileDoesNotExist();
        }
        System.out.println("Nodes parsed in " + (System.currentTimeMillis()-startTime)/1000.d + "s");
    }

    /**
     * parses the names.dmp file
     * adds names to the EXISTING tree
     * thus, readNodes always has to be called before readNames!
     */
    public void readNamesDmpFile(String fileNamesDmp) {
        long startTime = System.currentTimeMillis();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileNamesDmp));
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] lineSplit = line.split("\\|");
                String identifier = removeBlanksString(lineSplit[3]);
                if (identifier.equals("scientificname")){
                    int id = Integer.parseInt(removeBlanksString(lineSplit[0]));
                    String name = lineSplit[1].trim();
                    taxonTree.getTreeStructure().get(id).setName(name);
                }
            }
        } catch (IOException e) {
            printFileDoesNotExist();
        }
        System.out.println("Names parsed in " + (System.currentTimeMillis()-startTime)/1000.d + "s");
    }

    /**
     * Removes the entire white space from a String array.
     *
     * @param array - String array
     * @return the array without blanks
     */
    public static String[] removeBlanksFromArray(String[] array) {
        String[] arrayWithoutBlanks = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            arrayWithoutBlanks[i] = array[i].replaceAll("\\s+", "");
        }

        return arrayWithoutBlanks;
    }

    /**
     * Removes the entire white space from a String.
     *
     * @param stringToRemoveBlanksFrom - String
     * @return the String without blanks
     */
    public static String removeBlanksString(String stringToRemoveBlanksFrom) {

        return stringToRemoveBlanksFrom.replaceAll("\\s+", "");
    }

    public TaxonTree getTaxonTree() {
        return taxonTree;
    }
}
