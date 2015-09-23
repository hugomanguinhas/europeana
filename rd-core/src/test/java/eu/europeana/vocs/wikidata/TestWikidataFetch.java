package eu.europeana.vocs.wikidata;

import static eu.europeana.vocs.VocsUtils.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.europeana.vocs.wikidata.WikidataFetch;

public class TestWikidataFetch
{
    public static final void main(String... args) throws IOException
    {
        WikidataFetch fetch = new WikidataFetch(SPARQL_WIKIDATA);

        List<File> files = listFilesWithExtension(DIR_WIKIDATA, ".sparql", new ArrayList(20));
        for ( File f : files )
        {
            String name = getNameWithoutExtension(f);

            File dst = new File(f.getParentFile(), name + ".xml");
            if ( dst.exists() ) { continue; }

            System.out.println("Fetching: " + name + "...");
            fetch.fetchAll(getContent(f), "r", dst);
        }
    }
}
