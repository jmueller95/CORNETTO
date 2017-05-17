package model;

import java.util.ArrayList;

/**
 * Created by jm on 15.05.17.
 * Represents a taxon in the phylogenetic tree
 */
public class TaxonNode {
    private String name;
    private int taxonId;
    private String rank; //e.g. "kingdom", "species"
    private TaxonNode parentNode;
    private ArrayList<TaxonNode> childNodeList;

    public TaxonNode(String name, int taxonId, String rank, model.TaxonNode parentNode, ArrayList<TaxonNode> childNodeList) {
        this.name = name;
        this.taxonId = taxonId;
        this.rank = rank;
        this.parentNode = parentNode;
        this.childNodeList = childNodeList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTaxonId() {
        return taxonId;
    }

    public void setTaxonId(int taxonId) {
        this.taxonId = taxonId;
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

    public void setChildNodeList(ArrayList<TaxonNode> childNodeList) {
        this.childNodeList = childNodeList;
    }
}
