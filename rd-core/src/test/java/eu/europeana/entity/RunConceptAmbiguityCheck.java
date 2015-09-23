package eu.europeana.entity;

import eu.europeana.enrich.disamb.AmbiguityFetch;
import eu.europeana.enrich.disamb.SameAsDisambiguator;
import static eu.europeana.TestingResources.*;

public class RunConceptAmbiguityCheck
{
    public static final void main(String[] args)
    {
        new AmbiguityFetch(null, null, new SameAsDisambiguator()).
            process(FILE_CONCEPTS_DBPEDIA, FILE_CONCEPTS_DBPEDIA_AMBIGUITY
                  , FILE_CONCEPTS_DBPEDIA_CLUSTERS);
    }
}
