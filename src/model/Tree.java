package model;

import treeParser.NamesDmp;
import treeParser.NodesDmp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by julian on 15.05.17.
 */
public class Tree {
    private TaxonNode root;
    private HashMap<Integer, TaxonNode> treeStructure; //TODO: Not sure if name is appropriate, maybe refactor it

    public Tree(TaxonNode root){
        this.treeStructure = treeStructure;
        treeStructure.put(root.getTaxonId(), root);
    }

    //TODO

    /**
     *
     * @param node
     */
    public void addNode(TaxonNode node){

     if ( root == null ) {

         root = node;

     } else {

        treeStructure.put(node.getTaxonId(), node);

        //Searches the tree for every child of the new added node and adds the found child to the list of child notes.
        //It does also register the new node object as parent of the found child nodes

        for (Map.Entry<Integer, TaxonNode> entry : treeStructure.entrySet()) {

            if ( entry.getValue().getParentId() == node.getTaxonId() ) {

                TaxonNode foundChild = entry.getValue();

                foundChild.setParentNode(node); // Maybe not necessary

                node.getChildNodeList().add(foundChild);

            }

            //If the tree already contains the father of the new node, register this parent node object as parent node of the new node
            //and register the new node object as child of the found parent
            if ( entry.getValue().getTaxonId() == node.getParentId()) {

                TaxonNode foundParent = entry.getValue();

                node.setParentNode(foundParent);

                foundParent.getChildNodeList().add(node);
            }
        }

     }
    }


    /**
     * Builds the tree structure based on the parsed information from the NamesDmp and NodesDmp files
     * @param listOfNodesDmp - ArrayList of NodesDmp class.
     * @param listOfNamesDmp - ArrayList of NamesDmp class.
     */
    public void buildTree(ArrayList<NodesDmp> listOfNodesDmp, ArrayList<NamesDmp> listOfNamesDmp) {

        for ( int i = 0; i < listOfNodesDmp.size(); i++ ) {

            for ( int j = 0; j < listOfNamesDmp.size(); j++ ) {

                if ( listOfNodesDmp.get(i).getId() == listOfNamesDmp.get(j).getId() ) {

                    //if there's no root yet
                    addNode(new TaxonNode(listOfNamesDmp.get(j).getName(), listOfNamesDmp.get(i).getId(), listOfNodesDmp.get(i).getRank(),
                            /*null try out new way*/ listOfNodesDmp.get(i).getParentId(), new ArrayList<TaxonNode>()));

                }
            }

        }
        //tree.setRoot(new TaxonNode());
    }


    //GETTER

    public TaxonNode getRoot() {
        return root;
    }

    public HashMap<Integer, TaxonNode> getTreeStructure() { return treeStructure; }


    //SETTER

    public void setRoot(TaxonNode root) {
        this.root = root;
    }

}
