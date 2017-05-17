import sampleParser.BiomParser;

/**
 * Created by caspar on 17.05.17.
 */
public class MainTest {

    public static void main(String[] args) {
        System.out.println("Test Loading BIOME File");
        BiomParser bp = new BiomParser();
        bp.parse("src/test/testResources/example.biom");
    }
}