package sampleParser;

import model.Sample;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
//It's been quite a while since I last used interfaces, I hope I did it somehow correctly

/**
 * Created by julian on 15.05.17.
 */

public interface InputFile {

    ArrayList<Sample> parse(String filepath) throws IOException, ParseException;
    //TODO: What other methods might be useful?
}
