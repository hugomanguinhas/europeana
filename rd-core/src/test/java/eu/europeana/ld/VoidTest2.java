package eu.europeana.ld;

import static eu.europeana.vocs.VocsUtils.*;

import java.io.File;
import java.io.IOException;

public class VoidTest2 {

    public static final void main(String[] args) throws IOException
    {
        File file = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Linked Data\\void\\void_20150812.ttl");

        store(loadModel(null, file, "Turtle"), System.out);
    }
}
