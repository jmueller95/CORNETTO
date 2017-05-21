package treeParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import model.TaxonNode;
import model.Tree;

/**
 * Created by julian on 15.05.17.
 */
public class TreeParser {

    //set fileNamesDmp to filepath
    private String line = "";
    protected String fileNamesDmp = "";
    protected String fileNodesDmp = "";
    protected static ArrayList<NamesDmp> listOfNamesDmp = new ArrayList<>();
    protected static ArrayList<NodesDmp> listOfNodesDmp = new ArrayList<>();

    protected BufferedReader reader;
    public static Tree tree = new Tree(null);

    /*
    parses the names.dmp file
    creates the listOfNamesDmp
    adds ONLY the taxon that have a scientific name
     */
    public void readNamesDmpFile() {
        try {
            reader = new BufferedReader(new FileReader(fileNodesDmp));
            while ((line = reader.readLine()) != null) {
                String[] wholeLine = line.split("\\|");
                String[] wholeLineWithoutSpaces = removeBlanksFromArray(wholeLine);
                if (wholeLineWithoutSpaces[3].equals("scientificname")) {
                    int id = Integer.parseInt(wholeLineWithoutSpaces[0]);
                    String name = wholeLineWithoutSpaces[1];
                    String rank = wholeLineWithoutSpaces[2];

                    listOfNamesDmp.add(new NamesDmp(id, name, rank));
                }

            }
        } catch (IOException e) {
            System.err.println("The file does not exist.");
        }
    }

    /*
    parses the nodes.dmp file
    builds the tree by cross checking with the listOfNamesDmp
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

                listOfNodesDmp.add(new NodesDmp(id, parentId, rank));
            }
        } catch (IOException e) {
            System.err.println("The file does not exist.");
        }
    }

    public static void buildTree() {
        //TODO BUILD TREE
        //get information from the listOfNames
        for (int i = 0; i < listOfNodesDmp.size(); i++) {
            for (int j = 0; j < listOfNamesDmp.size(); j++) {
                if (listOfNodesDmp.get(i).getId() == listOfNamesDmp.get(j).getId()) {
                    //if there's no root yet
                    if (tree.getRoot() == null) {
                        tree.setRoot(new TaxonNode(listOfNamesDmp.get(j).getName(), listOfNamesDmp.get(i).getId(), listOfNodesDmp.get(i).getRank(), null, new ArrayList<TaxonNode>()));
                    } else { //there already has to be a root node
                        //TODO BUILD REST OF THE TREE
                    }
                }
            }

        }
        //tree.setRoot(new TaxonNode());
    }


    public static String[] removeBlanksFromArray(String[] array) {
        String[] arrayWithoutBlanks = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            arrayWithoutBlanks[i] = array[i].replaceAll("\\s+", "");
        }
        return arrayWithoutBlanks;
    }
}
