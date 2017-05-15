package sampleParser;

import model.Sample;

import java.util.ArrayList;
//It's been quite a while since I last used interfaces, I hope I did it somehow correctly

/**
 * Created by julian on 15.05.17.
 */
public interface InputFile {
    ArrayList<Sample> parse(String filepath);
    //TODO: What other methods might be useful?

}
