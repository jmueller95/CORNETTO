package treeParser;

import model.TaxonNode;
import model.TaxonTree;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by julian on 27.05.17.
 */
public class TreeParserTest {
    TreeParser treeParser = new TreeParser();

    @Test
    public void testParseTree() throws Exception {
        treeParser.parseTree("./res/nodes.dmp", "./res/names.dmp");
        //Check if node with id 7 has name Azorhizobium caulinodans and parent Azorhizobium
        TaxonTree taxonTree = treeParser.getTaxonTree();
        TaxonNode testNode = taxonTree.getNodeForID(7);
        assertEquals("Azorhizobium caulinodans", testNode.getName());
        assertEquals("species", testNode.getRank());
        assertEquals("Azorhizobium", testNode.getParentNode().getName());
        //Check if node 38 is actually linked to its (later) parent, 48
        testNode = taxonTree.getNodeForID(38);
        assertEquals("Archangium", testNode.getParentNode().getName());
    }

}