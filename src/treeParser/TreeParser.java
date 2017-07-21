package treeParser;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import model.TaxonNode;
import model.TaxonTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by julian on 15.05.17.
 */
public class TreeParser {
    //Tree to be created
    private TaxonTree taxonTree;
    public StringProperty progressProperty;

    public TreeParser() {
        this.taxonTree = new TaxonTree();
        progressProperty = new SimpleStringProperty("initialized");
    }

    //System
    private final void printFileDoesNotExist() {
        System.err.println("The file does not exist.");
    }

    /**
     * @param fileNodesDmp
     * @param fileNamesDmp parses the two files - makes sure that NodesDmp gets called before NamesDmp!
     */
    public void parseTree(String fileNodesDmp, String fileNamesDmp) {
        progressProperty.setValue("reading Nodes");
        readNodesDmpFile(fileNodesDmp);
        progressProperty.setValue("reading Names");
        readNamesDmpFile(fileNamesDmp);
    }

    /**
     * Default version of parseTree, uses ./res/nodes.dmp and ./res/names.dmp as paths
     */
    public void parseTree(){
        parseTree("./res/nodes.dmp", "./res/names.dmp");

    }


    /**
     * @param fileNodesDmp parses the nodes.dmp file
     *                     builds the tree (without names!)
     */
    private void readNodesDmpFile(String fileNodesDmp) {
        /*DEBUG*/
        long startTime = System.currentTimeMillis();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileNodesDmp));

            //First, read and create the root
            String line = reader.readLine();
            String[] lineSplit = line.split("\\|");
            int id = Integer.parseInt(lineSplit[0].trim());
            int parentId = Integer.parseInt(lineSplit[1].trim()); //Should also be 1 for the root
            String rank = lineSplit[2].trim();
            TaxonNode currentNode = new TaxonNode(id, rank, parentId);
            taxonTree.setRoot(currentNode);
            taxonTree.getTreeStructure().put(id, currentNode);

            //Parse the rest of the tree
            while ((line = reader.readLine()) != null) {
                lineSplit = line.split("\\|");
                id = Integer.parseInt(lineSplit[0].trim());
                parentId = Integer.parseInt(lineSplit[1].trim());
                rank = lineSplit[2].trim();
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
                taxonTree.getTreeStructure().put(parentId, parentNode);
                //Set parentNode as parent of currentNode
                currentNode.setParentNode(parentNode);
            }
        } catch (IOException e) {
            printFileDoesNotExist();
        }
        System.out.println("Nodes parsed in " + (System.currentTimeMillis() - startTime) / 1000.d + "s");
    }

    /**
     * parses the names.dmp file
     * adds names to the EXISTING tree
     * thus, readNodes always has to be called before readNames!
     */
    private void readNamesDmpFile(String fileNamesDmp) {
        long startTime = System.currentTimeMillis();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileNamesDmp));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineSplit = line.split("\\|");
                String identifier = lineSplit[3].trim();
                if (identifier.equals("scientific name")) {
                    int taxonId = Integer.parseInt(lineSplit[0].trim());
                    String name = lineSplit[1].trim();
                    taxonTree.getTreeStructure().get(taxonId).setName(name);
                }
            }
        } catch (IOException e) {
            printFileDoesNotExist();
        }
        System.out.println("Names parsed in " + (System.currentTimeMillis() - startTime) / 1000.d + "s");
    }

    public TaxonTree getTaxonTree() {
        return taxonTree;
    }
}
