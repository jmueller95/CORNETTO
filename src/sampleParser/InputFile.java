package sampleParser;

import model.Sample;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <h1>This is an interfance for the input file parsers</h1>
 * <p>
 * The class is dependant on a TaxonTree to be available!
 * When done parsing the file a list of samples is returned.
 * </p>
 *
 * @see BiomV1Parser
 * @see BiomV2Parser
 * @see TaxonId2CountCSVParser
 * @see ReadName2TaxIdCSVParser
 */
public interface InputFile {
    ArrayList<Sample> parse(String filepath) throws IOException, ParseException;
}
