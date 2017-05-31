package model;

import java.util.ArrayList;

/**
 * Created by jm on 15.05.17.
 * Represents a taxon in the phylogenetic tree
 */
public class TaxonNode {
    private String name;
    private int taxonId, parentId;
    private String rank; //e.g. "kingdom", "species"
    private TaxonNode parentNode;
    private ArrayList<TaxonNode> childNodeList;

    /**
     * Constructor for a Node added by the nodes.dmp-parser
     * Contains taxonId, rank, parentId
     * Creates an empty child list
     * Reference to parent is set right after this is called, name is set when names.dmp is parsed
     */
    public TaxonNode(int taxonId, String rank, int parentId){
        this.taxonId = taxonId;
        this.rank = rank;
        this.parentId = parentId;
        this.childNodeList = new ArrayList<>();
    }

    /**
     * Constructor for a parent node that hasn't actually been parsed yet
     * Only contains id and the first child
     * Rest is added when the node actually gets parsed
     */
    public TaxonNode(int taxonId, TaxonNode child){
        this.taxonId = taxonId;
        this.childNodeList = new ArrayList<>();
        childNodeList.add(child);
    }


    public String getName() {
        return name;
    }

    public int getTaxonId() {
        return taxonId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }
    public void setRank(String rank) {
        this.rank = rank;
    }

    public model.TaxonNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(model.TaxonNode parentNode) {
        this.parentNode = parentNode;
    }

    public ArrayList<TaxonNode> getChildNodeList() {
        return childNodeList;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}
