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

    /**
     * Creates a small TaxonTree and tests whether children are added correctly
     * @throws Exception
     */
    @Test
    public void testAddNode() throws Exception {
        TaxonNode rootNode = new TaxonNode("root", 1, "no rank", 1, new ArrayList<>());
        TaxonTree taxonTree = new TaxonTree(rootNode);
        TaxonNode childOfRoot = new TaxonNode("Child of root", 42, "testRank", 1, new ArrayList<>());
        taxonTree.addNode(childOfRoot);
        //The root should now be registered as parent of the newly added node
        assertEquals(rootNode, childOfRoot.getParentNode());
        //Furthermore, the new node should be in root's child list now
        assertEquals(childOfRoot, rootNode.getChildNodeList().get(0));

        //Let's do another generation and see if it still works
        TaxonNode grandchildOfRoot = new TaxonNode("Grandchild", 99, "testSubRank", 42, new ArrayList<>());
        taxonTree.addNode(grandchildOfRoot);
        //Check if it's a child of childOfRoot, but NOT of root itself
        assertEquals(childOfRoot, grandchildOfRoot.getParentNode());
        assertEquals(grandchildOfRoot,childOfRoot.getChildNodeList().get(0));
        for (TaxonNode node: rootNode.getChildNodeList()
             ) {
            assertNotEquals(grandchildOfRoot, node);
        }
    }

    /**
     * Creates a TaxonTree from the names_stub and the nodes_stub, which contain only the first 100 lines so that
     * computation doesn't take too long.
     * Tests some basic properties of the built Tree then.
     * @throws Exception
     */
    @Test
    public void testBuildTree() throws Exception {
       TreeParser treeParser = new TreeParser();
       /*Stub file*///treeParser.setFileNamesDmp("./res/testFiles/treeParser/names_stub.dmp");
       /*Stub file*///treeParser.setFileNodesDmp("./res/testFiles/treeParser/nodes_stub.dmp");
       /*Actual file*/ treeParser.setFileNamesDmp("./res/names.dmp");
       /*Actual file*/ treeParser.setFileNodesDmp("./res/nodes.dmp");
       treeParser.readNodesDmpFile();
       treeParser.readNamesDmpFile();
       TaxonNode rootNode = new TaxonNode("root", 1, "no rank", 1, new ArrayList<>());
       TaxonTree taxonTree = new TaxonTree(rootNode);
       taxonTree.buildTree(treeParser.getNodesDmps(), treeParser.getNamesDmps());

       //Check if value of id 7 matches: name="Azorhizobium caulinodans", rank="species", parent=6,
        //parentNode.name=Azorhizobium
       final HashMap<Integer, TaxonNode> treeStructure = taxonTree.getTreeStructure();
       TaxonNode testNode = treeStructure.get(7);
       assertEquals("Azorhizobium caulinodans", testNode.getName());
       assertEquals("species", testNode.getRank());
       assertEquals(6, testNode.getParentId());
       assertEquals("Azorhizobium", testNode.getParentNode().getName());
    }
}