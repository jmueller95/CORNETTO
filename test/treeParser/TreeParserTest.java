package treeParser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by julian on 27.05.17.
 */
public class TreeParserTest {
    TreeParser treeParser = new TreeParser();

    @Before
    public void setUp() throws Exception {
        treeParser.fileNamesDmp = "./res/names.dmp";
        treeParser.fileNodesDmp = "./res/nodes.dmp";

    }

    /**
     * Checks if the second entry in the namesDmp list matches: id=2 and name="Bacteria"
     * @throws Exception
     */
    @Test
    public void namesDmpTest() throws Exception {
        treeParser.readNamesDmpFile();
        NamesDmp firstNamesDmp = treeParser.namesDmps.get(1);
        assertEquals(2, firstNamesDmp.getId());
        assertEquals("Bacteria", firstNamesDmp.getName());
    }

    /**
     * Checks if the 32nd entry in the nodesDmp list matches: id=42, parentId=39, rank=genus
     * @throws Exception
     */
    @Test
    public void nodesDmpTest() throws Exception {
        treeParser.readNodesDmpFile();
        NodesDmp testNodesDmp = treeParser.nodesDmps.get(31);
        assertEquals(42,testNodesDmp.getId());
        assertEquals(39,testNodesDmp.getParentId());
        assertEquals("genus", testNodesDmp.getRank());
    }
}