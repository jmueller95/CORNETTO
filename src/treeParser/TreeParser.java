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
    protected ArrayList<NamesDmp> listOfNamesDmp = new ArrayList<>();

    protected BufferedReader reader;
    public Tree tree = new Tree(null, null);

    /*
    parses the names.dmp file
    creates the listOfNamesDmp
    side effect: adds "no sci name av" to the name if no scientific name is given
     */
    public void readNamesDmpFile(){
        try{
            reader = new BufferedReader(new FileReader(fileNodesDmp));
            while((line = reader.readLine()) != null){
                String[] wholeLine = line.split("\\|");
                String[] wholeLineWithoutSpaces = new String[wholeLine.length];
                for (int i = 0; i < wholeLine.length; i++){
                    wholeLineWithoutSpaces[i] = wholeLine[i].replaceAll("\\s+", "");
                }
                if(!wholeLineWithoutSpaces[3].equals("scientificname")){
                    wholeLineWithoutSpaces[1] += "no sci name av";
                }

                int id = Integer.parseInt(wholeLineWithoutSpaces[0]);
                String name = wholeLineWithoutSpaces[1];
                String rank = wholeLineWithoutSpaces[2];

               listOfNamesDmp.add(new NamesDmp(id, name, rank));
            }
        } catch (IOException e){
            System.err.println("The file does not exist.");
        }
    }

    /*
    parses the nodes.dmp file
    builds the tree by crosschecking with the listOfNamesDmp
     */
    public void readNodesDmpFile(){
        try{
            reader = new BufferedReader(new FileReader(fileNodesDmp));
            while((line = reader.readLine()) != null){
                String[] wholeLine = line.split("\\|");
                String[] wholeLineWithoutSpaces = new String[wholeLine.length];
                for (int i = 0; i < wholeLine.length; i++){
                    wholeLineWithoutSpaces[i] = wholeLine[i].replaceAll("\\s+", "");
                }
                int id = Integer.parseInt(wholeLineWithoutSpaces[0]);
                int parentId = Integer.parseInt(wholeLineWithoutSpaces[1]);
                String rank = wholeLineWithoutSpaces[2];

                //TODO BUILD TREE
                //if there's no root yet...
                if(tree.getRoot() == null){
                    //tree.setRoot(new TaxonNode());
                }
            }
        } catch (IOException e){
            System.err.println("The file does not exist.");
        }
    }
}
