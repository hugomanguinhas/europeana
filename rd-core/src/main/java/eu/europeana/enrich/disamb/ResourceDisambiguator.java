package eu.europeana.enrich.disamb;

import java.util.Comparator;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public interface ResourceDisambiguator extends Comparator<Resource>
{

    public void init(Model model);
}
