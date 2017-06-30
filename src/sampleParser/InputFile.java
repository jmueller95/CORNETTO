package sampleParser;

import model.Sample;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by julian on 15.05.17.
 */

public interface InputFile {
    ArrayList<Sample> parse(String filepath) throws IOException, ParseException;
    //TODO: What other methods might be useful?
}
