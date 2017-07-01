package model;


import java.util.HashMap;

/**
 * Created by julian on 15.05.17.
 */
public class TaxonTree {
    private TaxonNode root;
    private HashMap<Integer, TaxonNode> treeStructure;

    /**
     * Constructor with root, probably not used anymore
     *
     * @param root
     */
    public TaxonTree(TaxonNode root) {
        this.treeStructure = new HashMap<>();
        treeStructure.put(root.getTaxonId(), root);
        setRoot(root);
    }

    /**
     * Constructor without parameters, for we don't have any in the beginning
     */
    public TaxonTree() {
        this.treeStructure = new HashMap<>();
    }

    //GETTER
    public TaxonNode getRoot() {
        return root;
    }

    public HashMap<Integer, TaxonNode> getTreeStructure() {
        return treeStructure;
    }

    /**
     * Returns taxonNode for Node ID
     *
     * @param nodeID - Node id string parsed from sample files
     * @return returns taxon Node
     * @throws IllegalArgumentException - node id not found in tree
     */
    public TaxonNode getNodeForID(int nodeID) throws IllegalArgumentException {
        if (treeStructure.containsKey(nodeID)) {
            return treeStructure.get(nodeID);
        } else {
            throw new IllegalArgumentException("Node id " + nodeID + "was not found in tree");
        }
    }

    public TaxonNode getAncestorOfNode(TaxonNode taxonNode, String rank) {
        int currentNodeId = taxonNode.getTaxonId();
        TaxonNode currentNode = getNodeForID(currentNodeId);
        while(currentNode != root && !currentNode.getRank().equals(rank)){
            currentNodeId = currentNode.getParentId();
            currentNode = getNodeForID(currentNodeId);
        }
        return getNodeForID(currentNodeId);
    }

    //SETTER
    public void setRoot(TaxonNode root) {
        this.root = root;
    }

}
