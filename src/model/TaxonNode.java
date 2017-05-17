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

}
