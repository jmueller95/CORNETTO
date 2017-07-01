package model;

import org.junit.Before;
import org.junit.Test;
import treeParser.TreeParser;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by julian on 27.05.17.
 */
public class TaxonTreeTest {
    TaxonTree tree;

    @Before
    public void setUp() throws Exception {
        TreeParser treeParser = new TreeParser();
        treeParser.parseTree("./res/nodes.dmp", "./res/names.dmp");
        tree = treeParser.getTaxonTree();
    }

    /**
     * Tests if ancestor accession works correctly: If a node and a rank (as string) are given,
     * the method should return the node's ancestor on the respective level.
     * If the node doesn't have an ancestor with that rank, the root is returned.
     * @throws Exception
     */
    @Test
    public void testGetAncestorOfNode() throws Exception {
        TaxonNode testNode = tree.getNodeForID(17);
        TaxonNode ancestor = tree.getNodeForID(32011); //17's ancestor on family level
        assertEquals(ancestor, tree.getAncestorOfNode(testNode, "family"));
        assertEquals(tree.getRoot(), tree.getAncestorOfNode(testNode, "not_a_rank"));
    }
}