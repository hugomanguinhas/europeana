package eu.europeana.tf.corpus;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.europeana.tf.results.EnrichmentResult;
import static eu.europeana.tf.results.ResultUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Sep 2015
 */
public class GoldStandardGenerator
{
    private Model _model;

    public GoldStandardGenerator(File corpus)
    {
        _model = loadModel(corpus);
    }

    public void genGoldStandard(File source, File target)
    {
        GoldStandard gs = new GoldStandard();

        ArrayList<EnrichmentResult> list = new ArrayList();
        loadEnrichments("", source, list);

        List<Integer> indexes = getIndexes(gs.SIZE, list.size());
        System.out.println(indexes);

        for ( Integer index : indexes )
        {
            EnrichmentResult result = list.get(index);
            gs.add(new GoldStandard.Entry(result, _model, null));
        }
        System.out.println();
        gs.print(target);
    }

    private Model loadModel(File file)
    {
        Model m = ModelFactory.createDefaultModel();
        try {
            m.read(new FileInputStream(file), null, "RDF/XML");
        }
        catch (Exception e) { e.printStackTrace(); }
        return m;
    }

    private static List<Integer> getIndexes(int count, int max)
    {
        List<Integer> ret = new ArrayList<Integer>(Math.min(count, max));
        if ( max <= count )
        {
            for ( int i = 0; i < max; i++ ) { ret.add(i); }
            return ret;
        }
        Random r = new Random();
        while ( ret.size() < count )
        {
            int index = r.nextInt(max);
            if ( !ret.contains(index) ) { ret.add(index); }
        }
        return ret;
    }

    public static final void main(String[] args)
    {
        System.err.println(getIndexes(10, 20));
    }
}
