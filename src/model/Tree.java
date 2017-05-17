package model;

import java.util.HashMap;

/**
 * Created by julian on 15.05.17.
 */
public class Tree {
    TaxonNode root;
    HashMap<Integer, TaxonNode> idToNodeMap; //TODO: Not sure if name is appropriate, maybe refactor it

    public Tree(TaxonNode root, HashMap<Integer, TaxonNode> idToNodeMap) {
        this.root = root;
        this.idToNodeMap = idToNodeMap;
    }

    public TaxonNode getRoot() {
        return root;
    }

    public void setRoot(TaxonNode root) {
        this.root = root;
    }

    public HashMap<Integer, TaxonNode> getIdToNodeMap() {
        return idToNodeMap;
    }

    public void setIdToNodeMap(HashMap<Integer, TaxonNode> idToNodeMap) {
        this.idToNodeMap = idToNodeMap;
    }

    //TODO
    public void addNode(TaxonNode node){
        ;
        ;
        ;
    }
}
